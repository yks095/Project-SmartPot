import requests
from bs4 import BeautifulSoup
import pymysql

URL_PREFIX = 'https://terms.naver.com'

cnt = 0
urls = []

def insertflower(name, img, content):
    # MySQL Connection 연결
   conn = pymysql.connect(host='localhost', user='root', password='smartpot',
                          db='arduino', charset='utf8')

   # Connection 으로부터 Cursor 생성
   curs = conn.cursor()

   # SQL문 실행
   sql = "INSERT INTO dictionary(name, image, content) VALUES (%s, %s, %s)"
   curs.execute(sql, (name,img,content))

   conn.commit()
   conn.close()

def getInfo(url, cnt):
    target_url = URL_PREFIX + url
    req = requests.get(target_url)
    html = req.text
    soup = BeautifulSoup(html, 'html.parser')
    h2 = soup.find('h2', {'class': 'headword'})
    img = soup.find('img', {'id': 'relatedImageViewImg'})
    ps = soup.find_all('p', {'class': 'txt'})

    context = ''
    for p in ps:
        context+=p.text

    name = h2.text
    if img is  None:
        image='존재하지 않는 이미지'
    else: image = img['data-src']
    content = context

    cnt += 1
    print(str(cnt) +' '+ name)
    insertflower(name,image,content)
    return cnt


for i in range (1,80):
    req = requests.get('https://terms.naver.com/list.nhn?cid=42526&categoryId=42527&page=' + str(i))
    html = req.text
    soup = BeautifulSoup(html, 'html.parser')
    divs = soup.find_all('div', {'class': 'thumb_area'})
    for div in divs:
        a = div.find('a')
        urls.append(a['href'])

print("전체 갯수 : " + str(len(urls)))
for url in urls:
    cnt = getInfo(url, cnt)

import requests
from bs4 import BeautifulSoup
import pymysql
import re

URL_PREFIX = 'https://terms.naver.com'
urls = []
dic = {}

cnt = 0
image_len = 0

def getInfo(url, cnt, image_len):
    target_url = URL_PREFIX + url
    req = requests.get(target_url)
    html = req.text
    soup = BeautifulSoup(html, 'html.parser')


    # 태그
    th_list = []
    # 내용
    td_list = []

    ths = soup.find_all('th')
    tds = soup.find_all('td')

    img = soup.find('img', {'id': 'innerImage0'})
    if img is  None:
        image='존재하지 않는 이미지'
    else: image = img['data-src']

    if len(image) > image_len:
        image_len = len(image)

    for th in ths:
        data = th.text
        th_list.append(data)

    for td in tds:
        data = td.text
        data = re.sub('\n','',data).strip()
        td_list.append(data)

    for i in range(len(td_list)):
        th = th_list[i] # 테그
        td = td_list[i] # 내용
        if th not in dic: dic[th]=len(td); continue
        if len(td) > dic[th]:
            dic[th]=len(td)

    return cnt, image_len


for i in range (1,22):
    req = requests.get('https://terms.naver.com/list.nhn?cid=42526&categoryId=59892&so=st3.asc&viewType=&categoryType=76&page=' + str(i))
    html = req.text
    soup = BeautifulSoup(html, 'html.parser')
    divs = soup.find_all('div', {'class': 'thumb_area'})
    for div in divs:
        a = div.find('a')
        urls.append(a['href'])

print("#FLOWERS : " + str(len(urls)))
for url in urls:
    cnt, image_len = getInfo(url, cnt, image_len)

DDL=open('DDL.sql','w',encoding='utf-8')
DDL.write('CREATE TABLE `dictionary` (\n\t`name` varchar(50) DEFAULT NULL,\n\t`image` varchar(200) DEFAULT NULL,\n\t')

index = 0
for num in dic.values():
    index=index+1
    DDL.write('`ATTR_' + str(index) + '`' + ' varchar(' + str(num) + ')' + ' DEFAULT NULL,\n\t')
DDL.write(');\n\n\n')

index = 0
DDL.write('flower_info = {\n');
for tag in dic.keys():
    index=index+1
    DDL.write('\t\'' + tag +  '\': \'ATTR_' + str(index) + '\',\n');
DDL.write('}\n\nimage_len:' + str(image_len));

DDL.close()

print(dic)
print('#ATTRS : ' + str(len(dic)))

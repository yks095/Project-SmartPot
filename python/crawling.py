import requests
from bs4 import BeautifulSoup
import pymysql
import re

mysql_config = {
    'host': 'localhost',
    'user': 'root',
    'password': 'ssuk0625',
    'db': 'android',
    'charset': 'utf8',
    'port': 3307
}

URL_PREFIX = 'https://terms.naver.com'
dic,flower_info = {},{}
image_len,cnt = 0,0
Dictionary=open('Dictionary.sql','w',encoding='utf-8')

# WEB PAGE URL CRAWLING
def crawling_sub_page_url():
    urls = []
    for i in range (1,22):
        req = requests.get('https://terms.naver.com/list.nhn?cid=42526&categoryId=59892&so=st3.asc&viewType=&categoryType=76&page=' + str(i))
        html = req.text
        soup = BeautifulSoup(html, 'html.parser')
        divs = soup.find_all('div', {'class': 'thumb_area'})
        for div in divs:
            a = div.find('a')
            urls.append(a['href'])
        # end for
    # end for
    return urls
# end def

# CRAWLING TH, TD AND get TD MAX_LENGTH
def crawling_tag_len(url, image_len):
    th_list = []
    td_list = []
    target_url = URL_PREFIX + url
    req = requests.get(target_url)
    html = req.text
    soup = BeautifulSoup(html, 'html.parser')

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

    return image_len

# CREATE DICTIONARY TABLE
def creat_table_dictionary(creat_query):
    # MySQL Connection 연결
    conn = pymysql.connect(host=mysql_config['host'], user=mysql_config['user'], password=mysql_config['password'],
                          db=mysql_config['db'], charset=mysql_config['charset'], port=mysql_config['port'])
    # Connection 으로부터 Cursor 생성
    curs = conn.cursor()

    curs.execute(creat_query)
    conn.commit()
    conn.close()
# end def

# CRAWLING DATA NAME, IMAGE_URL, ATTRS
def crawling_data(url, cnt):
    target_url = URL_PREFIX + url
    req = requests.get(target_url)
    html = req.text
    soup = BeautifulSoup(html, 'html.parser')
    h2 = soup.find('h2', {'class': 'headword'})
    img = soup.find('img', {'id': 'innerImage0'})

    # 태그
    th_list = []
    # 내용
    td_list = []

    ths = soup.find_all('th')
    tds = soup.find_all('td')

    name = h2.text

    if img is  None:
        image='존재하지 않는 이미지'
    else: image = img['data-src']

    for th in ths:
        data = th.text
        th_list.append(data)

    for td in tds:
        data = td.text
        data = re.sub('\n','',data).strip()
        td_list.append(data)

    cnt += 1
    insertflower(name,image,th_list,td_list)

    return cnt
# end def

# INSERT DATA IN DICTIONARY TABLE
def insertflower(name, img, th_list, td_list):
    # MySQL Connection 연결
    conn = pymysql.connect(host=mysql_config['host'], user=mysql_config['user'], password=mysql_config['password'],
                          db=mysql_config['db'], charset=mysql_config['charset'], port=mysql_config['port'])

    # Connection 으로부터 Cursor 생성
    curs = conn.cursor()

    name=re.sub('\'', '', name)
    attrs = ''
    values = '\'' + name + '\',\'' + img + '\''

    for attr in th_list:
       attrs = attrs + ',' + flower_info[attr]

    for value in td_list:
        value = re.sub('\'', '', value)
        values = values + ',\'' + value + '\''

    sql = "INSERT INTO dictionary(name, image" + attrs + ") VALUES (" + values + ")"
    Dictionary.write(sql+'\n')

    try:
        curs.execute(sql)
        conn.commit()
        conn.close()
        print('Inset Success : ' + '#'+str(cnt) +name)

    except:
        print('Inset Fail : ' + '#'+str(cnt)+name)
# end def


urls = crawling_sub_page_url()
print('Get SubPageUrl --SUCCESS--')
print('Total Pages : ' + str(len(urls)) , end='\n\n')

for url in urls:
    image_len = crawling_tag_len(url, image_len)
print("Crawling Tag Len --SUCCESS--" , end='\n\n')

creat_query = 'CREATE TABLE `dictionary` (\n\t`name` varchar(50) DEFAULT NULL,\n\t`image` varchar('+ str(image_len) +') DEFAULT NULL,\n\t'
for idx, num in enumerate(dic.values()):
    if idx==len(dic)-1: creat_query = creat_query + '`ATTR_' + str(idx) + '`' + ' varchar(' + str(num) + ')' + ' DEFAULT NULL\n);\n\n\n'; continue
    creat_query = creat_query + '`ATTR_' + str(idx) + '`' + ' varchar(' + str(num) + ')' + ' DEFAULT NULL,\n\t'

Dictionary.write(creat_query)
creat_table_dictionary(creat_query)
print("Creat Table Dictionary --SUCCESS--" , end='\n\n')


for idx, tag in enumerate(dic.keys()):
    flower_info[tag]='ATTR_' + str(idx)


for url in urls:
    cnt = crawling_data(url, cnt)
print("----DONE----")

Dictionary.close()

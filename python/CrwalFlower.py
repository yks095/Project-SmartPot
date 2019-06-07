import requests
from bs4 import BeautifulSoup
import pymysql
import re

URL_PREFIX = 'https://terms.naver.com'

flower_info = {
    '학명': 'ATTR_1',
    '과명': 'ATTR_2',
    '유통명': 'ATTR_3',
    '관리수준': 'ATTR_4',
    '관리요구': 'ATTR_5',
    '생장속도': 'ATTR_6',
    '생육온도': 'ATTR_7',
    '최저온도': 'ATTR_8',
    '광요구도': 'ATTR_9',
    '배치 장소': 'ATTR_10',
    '물주기': 'ATTR_11',
    '비료정보': 'ATTR_12',
    '번식 방법': 'ATTR_13',
    '번식 시기': 'ATTR_14',
    '병충해관리정보': 'ATTR_15',
    '습도': 'ATTR_16',
    '토양': 'ATTR_17',
    '원산지': 'ATTR_18',
    '분류': 'ATTR_19',
    '생육형태': 'ATTR_20',
    '생장높이(cm)': 'ATTR_21',
    '생장너비(cm)': 'ATTR_22',
    '실내정원구성': 'ATTR_23',
    '생태형': 'ATTR_24',
    '잎형태': 'ATTR_25',
    '잎무늬': 'ATTR_26',
    '잎색': 'ATTR_27',
    '꽃피는 계절': 'ATTR_28',
    '꽃색': 'ATTR_29',
    '열매 맺는 계절': 'ATTR_30',
    '열매색': 'ATTR_31',
    '향기': 'ATTR_32',
    'TIP': 'ATTR_33',
    '특별관리정보': 'ATTR_34',
    '독성': 'ATTR_35',
    '병충해 관리': 'ATTR_36',
    '생장형': 'ATTR_37',
    '번식': 'ATTR_38',
    '식물 분류': 'ATTR_39',
    '형태 분류': 'ATTR_40',
    '엽색 변화': 'ATTR_41',
    '뿌리형태': 'ATTR_42',
    '꽃': 'ATTR_43',
    '고온다습': 'ATTR_44',
}

cnt = 0
urls = []
fail_flower_list = []

fail_log = open('fail_log.sql', 'w' , encoding='utf-8')

def insertflower(name, img, th_list, td_list):
    # MySQL Connection 연결
    conn = pymysql.connect(host='localhost', user='root', password='smartpot',
                          db='arduino', charset='utf8', port=3307)

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

    # SQL문 실행
    sql = "INSERT INTO dictionary(name, image" + attrs + ") VALUES (" + values + ")"

    try:
        curs.execute(sql)
        conn.commit()
        conn.close()
        print('inset success:' + '#'+str(cnt) +name)

    except:
        print('inset fail:' + '#'+str(cnt)+name)
        fail_flower_list.append(name)
        fail_log.write('NAME:' + name + '\n')
        fail_log.write('SQL:\n' + sql + '\n\n')

# end def

def getInfo(url, cnt):
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


for i in range (1,22):
    req = requests.get('https://terms.naver.com/list.nhn?cid=42526&categoryId=59892&so=st3.asc&viewType=&categoryType=76&page=' + str(i))
    html = req.text
    soup = BeautifulSoup(html, 'html.parser')
    divs = soup.find_all('div', {'class': 'thumb_area'})
    for div in divs:
        a = div.find('a')
        urls.append(a['href'])

print("#all_flowers : " + str(len(urls)))
for url in urls:
    cnt = getInfo(url, cnt)

fail_log.close()
print('#fail_flower : ' + str(len(fail_flower_list)))
print('fail_flower : ' + str(fail_flower_list))

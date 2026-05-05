import urllib.request
import urllib.parse
import http.cookiejar
import urllib.error

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# login
data = urllib.parse.urlencode({'email':'admin@gmail.com', 'password':'admin123'}).encode('utf-8')
opener.open('http://localhost:8080/login', data)

endpoints = ['/tasks', '/plans', '/admin/users']
for ep in endpoints:
    try:
        r = opener.open('http://localhost:8080' + ep)
        print(f"{ep} OK")
    except urllib.error.HTTPError as e:
        print(f"Error {e.code} on {ep}")
        with open(f"out_{ep.replace('/', '_')}.html", "w", encoding='utf-8') as f:
            f.write(e.read().decode('utf-8'))

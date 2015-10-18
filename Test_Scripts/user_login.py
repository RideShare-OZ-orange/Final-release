import urllib
import urllib2

#  url = 'http://localhost:3000/ride/request'
url = 'http://144.6.226.237/user/login'
values = dict(
    username='maxzhx@gmail.com',
)
# url = 'http://127.0.0.1/login.php'

data = urllib.urlencode(values)
req = urllib2.Request(url, data)

rsp = urllib2.urlopen(req)
content = rsp.read()
print content

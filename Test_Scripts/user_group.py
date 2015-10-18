import urllib
import urllib2

#  url = 'http://localhost:3000/user/getGroups'
url = 'http://144.6.226.237/user/getGroups'
values = dict(
    username='maxzhx@gmail.com',
)

data = urllib.urlencode(values)
req = urllib2.Request(url, data)

rsp = urllib2.urlopen(req)
content = rsp.read()
print content

import urllib
import urllib2

url = 'http://localhost:3000/group/request'
#  url = 'http://144.6.226.237/group/create'
values = dict(
    username='maxzhx@gmail.com',
    group_id='5619d370192e12a9e00d6d44'
)

data = urllib.urlencode(values)
req = urllib2.Request(url, data)

rsp = urllib2.urlopen(req)
content = rsp.read()
print content

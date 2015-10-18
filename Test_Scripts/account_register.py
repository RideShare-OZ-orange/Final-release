import urllib
import urllib2

url = 'http://localhost:3000/register'
#  url = 'http://144.6.226.237/group/create'
#  name,g_lon.g_lat,introduction,admin_id,location
values = dict(
    username='admin',
    password='admin'
)

data = urllib.urlencode(values)
req = urllib2.Request(url, data)

rsp = urllib2.urlopen(req)
content = rsp.read()
print content

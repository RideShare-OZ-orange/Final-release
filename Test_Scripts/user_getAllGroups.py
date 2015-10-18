import urllib
import urllib2

#  url = 'http://localhost:3000/user/getAllGroups'
url = 'http://144.6.226.237/user/getGroups'
values = dict(
    username='george.nader@gmail.com',
)
#  values = dict(
#      username='george.nader@gmail.com',
#      p_lon="144.980822",
#      p_lat="-37.870153",
#      note="My friend, can I join you?",
#      pickup_add="Carlisle St, St Kilda East VIC 3183",
#      pickup_time="2015-10-04T09:35:00.000Z",
#      ride_id="55ebccc1627cfa34678b4bf2"
#  )
#  values = dict(
#      username='junjiex@student.unimelb.edu.au',
#      p_lon="145.213852",
#      p_lat="-37.989568",
#      note="Hi!",
#      pickup_add="8 William Ave, Dandenong South VIC 3175",
#      pickup_time="2015-10-04T09:35:00.000Z",
#      ride_id="5605fb50bc5407ae6d97bd50"
#  )
#  values = dict(
#      username='qianz7@student.unimelb.edu.au',
#      p_lon="144.654922",
#      p_lat="-37.911497",
#      note="I wanna go to MelUni!",
#      pickup_add="2 Doolan St, Werribee VIC 3030",
#      pickup_time="2015-10-04T09:25:00.000Z",
#      ride_id="5605fb50bc5407ae6d97bd50"
#  )

data = urllib.urlencode(values)
req = urllib2.Request(url, data)

rsp = urllib2.urlopen(req)
content = rsp.read()
print content


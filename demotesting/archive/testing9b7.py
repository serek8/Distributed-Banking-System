#####################################
###  Info v1
###  a=bab2501 v= 1.0 d= 2018.12.08
####################################

###
# Dependencies 
# => pip install requests
#########################
import requests
import time

###
# Global Settings
#########################

host = "145.100.111.54"
protocol = "http"
port = "8080"
baseurl = protocol + "://" + host + ":" + port

###
# Standard Functions (theAPI) 
#########################
def balanceAccount(accountNumber):
	url = baseurl+"/accounts/"+str(accountNumber)+"/balance"
	#print(url)
	#jdata={"amount" : str(amount)}
	#r = requests.post(url,json=jdata)
	r = requests.get(url)
	print("Status: " + str(r.status_code) + "Message: " + r.text)
	return "";

def balanceAccountRt(accountNumber):
        url = baseurl+"/accounts/"+str(accountNumber)+"/balance"
        #print(url)
        #jdata={"amount" : str(amount)}
        #r = requests.post(url,json=jdata)
        r = requests.get(url)
        #print("Status: " + str(r.status_code) + "Message: " + r.text)
        try:
		return float(r.text)
	except:
		return 600001.1



###
# Test 7
#########################

def test(run, acc):
	while (balanceAccountRt(acc) != 60000):
		print("waitAreceive" + str(acc+1))
		#time.sleep(0.1)
	start = time.time()
	print("start" + str(start))
	while (balanceAccountRt(acc+1) < 60000):
		print balanceAccountRt(acc+1)
		time.sleep(0.1)
	end = time.time()
	print("end" + str(end))
	differ = end - start;
	print("time" + str(differ))
	f = open('results.log', 'a')
	f.write("receive,"+ str(run) + ","  + str(acc) + ","  + str(acc+1) + "," + str(start) + "," + str(end) + "," + str(differ) + "," )
	f.write("\n")
	f.close()
	return True;


def start(acc):
	#balanceAccount(acc)
	while (balanceAccountRt(acc) != 60000):
		print(time.time())
	print("" + str(acc) + "Okay:" + str(balanceAccountRt(acc)))
	time.sleep(2)
	return True





countt = 0

while True:
	countt = countt+1
	print("\n\n\n\n\n\n\n\n\n")
	print(time.ctime())
	print("7")
	test(countt,7)
	time.sleep(30)


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

def test(acc):
	while (balanceAccountRt(acc) != 60000):
		print("waitA" + str(acc))
		#time.sleep(0.1)
	start = time.time()
	print("start" + str(start))
	while (balanceAccountRt(acc) > 59000):
		print balanceAccountRt(acc)
		time.sleep(0.1)
	end = time.time()
	print("end" + str(end))
	differ = end - start;
	print("time" + str(differ))
	return True;


def start(acc):
	#balanceAccount(acc)
	while (balanceAccountRt(acc) != 60000):
		print(time.time())
	print("" + str(acc) + "Okay:" + str(balanceAccountRt(acc)))
	time.sleep(2)
	return True





while True:
	print("\n\n\n\n\n\n\n\n\n")
	print(time.ctime())
	print("5")
	test(5)
	time.sleep(1)
	print("6")
	test(6)
	time.sleep(5)
	print("7")
	test(7)
	time.sleep(5)


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
        return float(r.text);



###
# Info Display
#########################

def info(acc):
	#balanceAccount(acc)
	while (balanceAccountRt(acc) != 60000):
		print(str(acc) + "Not Okay:" + str(balanceAccountRt(acc)))
		time.sleep(0.1)
	print("" + str(acc) + "Okay:" + str(balanceAccountRt(acc)))
	time.sleep(2)
	return True


while True:
	print("\n\n\n\n\n\n\n\n\n")
	print(time.ctime())
	print("1")
	info(1)
	print("2")
	info(2)
	print("3")
	info(3)
	print("4")
	info(4)
	print("5")
	info(5)
	print("6")
	info(6)
	print("7")
	info(7)
	print("8")
	info(8)
	print("9")
	info(9)
	print("10")
	info(10)
	print("11")
	info(11)
	print("12")
	info(12)
	print("99")
	balanceAccount(99)
	time.sleep(5)


#####################################
###  Demo Tester Side B: Logger v4
###  a=bab2501 v= 4.0 d= 2018.12.19
####################################

###
# Dependencies 
# => pip install requests
#########################
import requests
import time
import sys

###
# Global Settings
#########################

host = "145.100.111.54"
protocol = "http"
port = "8080"
baseurl = protocol + "://" + host + ":" + port

###
# CLI Settings
#########################

try:
    sys.argv[1]
except NameError:
    mode = 'r'
else:
    mode = sys.argv[1]  # r = receive / s = send
	
try:
    sys.argv[2]
except NameError:
    accountNumber = '1'
else:
    accountNumber = sys.argv[2] #accountNumber to attatch to (receive)
	
try:
    sys.argv[3]
except NameError:
    accountNumberDestination = accountNumber + 1
else:
    accountNumberDestination = sys.argv[3] #accountNumberDestination to attatch to (sendmode)


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
		return 600001.1 #Error code without create Error

###
# Test 7
#########################

def sendTest(run, accr, accs):
	while (balanceAccountRt(accs) != 60000):
		print("waitAsend" + str(accs))
		#time.sleep(0.1)
	start = time.time()
	print("start" + str(start))
	while (balanceAccountRt(accs) < 60000):
		print balanceAccountRt(accs)
		time.sleep(0.1)
	end = time.time()
	print("end" + str(end))
	differ = end - start;
	print("time" + str(differ))
	f = open('results.log', 'a')
	f.write("send,"+ str(run) + ","  + str(accs) + ","  + str(accs) + "," + str(start) + "," + str(end) + "," + str(differ) + "," )
	f.write("\n")
	f.close()
	return True;
	
def receiveTest(run, accr, accs):
	while (balanceAccountRt(accr) != 60000):
		print("waitAreceive" + str(accr))
		#time.sleep(0.1)
	start = time.time()
	print("start" + str(start))
	while (balanceAccountRt(accr) < 60000):
		print balanceAccountRt(accr)
		time.sleep(0.1)
	end = time.time()
	print("end" + str(end))
	differ = end - start;
	print("time" + str(differ))
	f = open('results.log', 'a')
	f.write("receive,"+ str(run) + ","  + str(accr) + ","  + str(accs) + "," + str(start) + "," + str(end) + "," + str(differ) + "," )
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

round = 0
while True:
	round = round+1
	print("\n\n\n\n\n\n\n\n\n")
	print(time.ctime())
	print("5")
	if(mode=="r"):
		receiveTest(round,accountNumber,accountNumberDestination)
	if(mode=="s"):
		sendTest(round,accountNumber,accountNumberDestination)
	time.sleep(30)


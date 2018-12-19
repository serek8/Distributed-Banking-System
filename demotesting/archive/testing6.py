#####################################
###  Testing v1
###  a=bab2501 v= 1.0 d= 2018.12.08
####################################

###
# Dependencies 
# => pip install requests
#########################
import requests

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
def createAccount(accountNumber):
	url = baseurl+"/accounts/create"
	jdata={"accountNumber": str(accountNumber)}
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- createAccount " + str(accountNumber))

def balanceAccount(accountNumber):
	url = baseurl+"/accounts/"+str(accountNumber)+"/balance"
	#print(url)
	#jdata={"amount" : str(amount)}
	#r = requests.post(url,json=jdata)
	r = requests.get(url)
	print("Status: " + str(r.status_code) + " Message: " + r.text)
	#return float(r.text);

def depositAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/deposit"
	jdata={"accountNumber," : str(accountNumber), "amount" : amount}
	#print(url)
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- depositAccount " + str(amount) + " to " + str(accountNumber))

def withdrawAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/withdraw"
	jdata={"amount" : amount}
	#print(url)
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- withdrawAccount " + str(amount) + " from " + str(accountNumber))

def transferAccount(accountNumber, amount, accountNumberDestination):
	url = baseurl+"/accounts/"+str(accountNumber)+"/transfer"
	jdata={"amount" : amount, "accountNumberDestination" : str(accountNumberDestination)}
	#print(url)	
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- transferAccount " + str(amount) + " from " + str(accountNumber) + " to " + str(accountNumberDestination))
	
###
# Scenario
#########################

def scenarioA(accountNumber = 1):
	print("= senerioA =")
	balanceAccount(accountNumber)
	createAccount(accountNumber)
	balanceAccount(accountNumber)
	createAccount(accountNumber)
	balanceAccount(accountNumber)

def scenarioB(accountNumber = 1):
	print("= senerioB =")
	balanceAccount(accountNumber)
	depositAccount(accountNumber, 200)  #should happen
	withdrawAccount(accountNumber, 50)  #should happen
	withdrawAccount(accountNumber, 50)  #should happen
	withdrawAccount(accountNumber, 50)  #should happen
	withdrawAccount(accountNumber, 50)  #should happen
	withdrawAccount(accountNumber, 150) #should NOT happen
	balanceAccount(accountNumber)

def scenarioC(accountNumber = 1):
	print("= senerioC =")
	while True:
		depositAccount(accountNumber, 400)  #should happen
		withdrawAccount(accountNumber, 200)  #should happen
		withdrawAccount(accountNumber, 200)  #should happen
		break;
	balanceAccount(accountNumber)
	print("end senerioC")

def scenarioD(accountNumber = 1,accountNumberDestination = 2, amount = 100):
	print("= senerioD =")
	balanceAccount(accountNumber)
	balanceAccount(accountNumberDestination)
	while True:
		depositAccount(accountNumber, amount)  						#should happen
		transferAccount(accountNumber, amount, accountNumberDestination)	#should happen
		withdrawAccount(accountNumber, amount)						#should NOT happen
		withdrawAccount(accountNumberDestination, amount)			#should happen
		break;
	balanceAccount(accountNumber)
	balanceAccount(accountNumberDestination)
	print("end senerioD")

## Setup
#createAccount(1)
#createAccount(2)

for lp in range(1000):
	#depositAccount(lp, 1000)
	createAccount(lp)

#depositAccount(2, 200)

#createAccount(2)
#createAccount(3)
#ccreateAccount(5)
#createAccount(6)
#createAccount(7)
#createAccount(8)
#createAccount(9)
#createAccount(10)
#createAccount(11)
#createAccount(12)
#createAccount(13)

#depositAccount(2, 200)

#while True:
#	depositAccount(2, 200)
#	withdrawAccount(2, 100)

#scenarioA();
#scenarioB();
#scenarioC();
#scenarioD();

#print("reset")
#balanceAccount(1)
#balanceAccount(2)
#depositAccount(1, 100)
#withdrawAccount(1, 100)
#balanceAccount(1)
#balanceAccount(2)


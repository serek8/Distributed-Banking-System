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

host = "127.0.0.1"
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
	print(str(r.status_code) + r.text + "createAccount " + str(accountNumber))

def balanceAccount(accountNumber):
	url = baseurl+"/accounts/"+str(accountNumber)+"/balance"
	print(url)
	#jdata={"amount" : str(amount)}
	#r = requests.post(url,json=jdata)
	r = requests.get(url)
	print(str(r.status_code) + r.text)
	return 0.0;

def depositAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/deposit"
	jdata={"accountNumber," : str(accountNumber), "amount" : amount}
	print(url)
	r = requests.post(url,json=jdata)
	print(str(r.status_code) + r.text + "depositAccount " + str(amount) + " to " + str(accountNumber))

def withdrawAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/withdraw"
	jdata={"amount" : amount}
	print(url)
	r = requests.post(url,json=jdata)
	print(str(r.status_code) + r.text + "withdrawAccount " + str(amount) + " from " + str(accountNumber))

def transferAccount(accountNumber, amount, accountNumberDestination):
	url = baseurl+"/accounts/"+str(accountNumber)+"/transfer"
	jdata={"amount" : amount, "accountNumberDestination" : str(accountNumberDestination)}
	print(url)	
	r = requests.post(url,json=jdata)
	print(str(r.status_code) + r.text + "transferAccount " + str(amount) + " from " + str(accountNumber) + " to " + str(accountNumberDestination))
	
#TODO: transaction

###
# Scenario
#########################

def scenarioA(accountNumber = 1):
	print("= senerioA =")
	createAccount(accountNumber)
	createAccount(accountNumber)

def scenarioB(accountNumber = 1):
	print("= senerioB =")
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
	while True:
		depositAccount(accountNumber, amount)  						#should happen
		transferAccount(accountNumber, amount, accountNumberDestination)	#should happen
		withdrawAccount(accountNumber, amount)						#should NOT happen
		withdrawAccount(accountNumberDestination, amount)			#should happen
		break;
	balanceAccount(accountNumber)
	print("end senerioD")

scenarioA();
scenarioB();
scenarioC();
scenarioD();


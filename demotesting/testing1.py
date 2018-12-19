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

def depositAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/deposit"
	json={"amount" : amount}
	r = requests.post(url,json)
	print(str(r.status_code) + r.text + "depositAccount " + str(amount) + " to " + str(accountNumber))

def withdrawAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/withdraw"
	json={"amount" : amount}
	r = requests.post(url,json)
	print(str(r.status_code) + r.text + "withdrawAccount " + str(amount) + " from " + str(accountNumber))

#TODO: transaction

###
# Scenario
#########################

def scenarioA(accountNumber = 1):
	print("senerioA")
	createAccount(accountNumber);
	createAccount(accountNumber);

scenarioA();

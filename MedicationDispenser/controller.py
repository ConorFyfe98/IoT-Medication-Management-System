import RPi.GPIO as GPIO
import sys
import time
import datetime
from datetime import timedelta
import multiprocessing
import mysql.connector
import threading
import json
from urllib import request, parse

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

#Set variables and and pins
buttonPin = 26
buzzerPin = 11
ledPin = 12
pillOnePin = 17
pillTwoPin =27
pillThreePin = 22
waitTime = 10
pillOneAmount = int(sys.argv[1])
pillTwoAmount = int(sys.argv[2])
pillThreeAmount = int(sys.argv[3])
medicationID = int(sys.argv[4])

GPIO.setup(buttonPin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
GPIO.setup(buzzerPin,GPIO.OUT)
GPIO.setup(ledPin, GPIO.OUT)
GPIO.setup(pillOnePin, GPIO.OUT)
GPIO.setup(pillTwoPin, GPIO.OUT)
GPIO.setup(pillThreePin, GPIO.OUT)

#Update database via API
def updateDB(medicationStatus):
        print("Updating database...")
        datalog={}
        datalog["status"]=medicationStatus
        datalog["medicationID"]=medicationID
        datalog=json.dumps(datalog)
        myData=[('Data',datalog)]
        data = parse.urlencode(myData).encode()
        req = request.Request("https://mayar.abertay.ac.uk/~1604475/MedWeb/API/updateStatus.php", data=data)
        with request.urlopen(req) as response:
                page = response.read().decode()
        print(page) #Print result from page


def my_callback(buttonPin):
        GPIO.output(ledPin, GPIO.LOW)
        GPIO.output(buzzerPin, GPIO.LOW)
        medicationStatus = "Taken"
        updateDB(medicationStatus)
        pM.terminate()

#Dispensing pill function by turning on/off solenoids
def pillOneDispense(pillOneAmount):
        for x in range(pillOneAmount):
                GPIO.output(pillOnePin, GPIO.HIGH)
                time.sleep(1)
                GPIO.output(pillOnePin, GPIO.LOW)
                time.sleep(1)

def pillTwoDispense(pillTwoAmount):
        for x in range(pillTwoAmount):
                GPIO.output(pillTwoPin, GPIO.HIGH)
                time.sleep(1)
                GPIO.output(pillTwoPin, GPIO.LOW)
                time.sleep(1)

def pillThreeDispense(pillThreeAmount):
        for x in range(pillThreeAmount):
                GPIO.output(pillThreePin, GPIO.HIGH)
                time.sleep(1)
                GPIO.output(pillThreePin, GPIO.LOW)
                time.sleep(1)

#Sleep for set time and turn off led, buzzer then update database once complete
def pillMissedCheck(waitTime):
        time.sleep(waitTime)
        GPIO.output(ledPin, GPIO.LOW)
        GPIO.output(buzzerPin, GPIO.LOW)
        print("Medication Missed")
        medicationStatus = "Missed"
        updateDB(medicationStatus)
        sys.exit()

#Multi-threading for dispensing        
if __name__ == "__main__":
        print("Dispensing Medication...")
        p1 = multiprocessing.Process(target=pillOneDispense, args=(pillOneAmount, ))
        p2 = multiprocessing.Process(target=pillTwoDispense, args=(pillTwoAmount, ))
        p3 = multiprocessing.Process(target=pillThreeDispense, args=(pillThreeAmount, ))
        
        p1.start()
        p2.start()
        p3.start()

        p1.join()
        p2.join()
        p3.join()

        print("Medication Dispensed")

#Turn on LED and Buzzer pin
GPIO.output(ledPin, GPIO.HIGH)
GPIO.output(buzzerPin, GPIO.HIGH)
#Activate button and start multi-thread
GPIO.add_event_detect(buttonPin, GPIO.RISING, callback=my_callback, bouncetime=2000)
pM = multiprocessing.Process(target=pillMissedCheck, args=(waitTime, ))
pM.start()


medicationTime = datetime.datetime.now() + datetime.timedelta(minutes = 15)
print("Button must be pressed by: " + medicationTime.strftime("%Y-%m-%d %H:%M:%S"))


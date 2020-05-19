from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient
import time
import os
import logging
import RPi.GPIO as GPIO

from time import sleep

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

ledPin = 12
GPIO.setup(ledPin, GPIO.OUT)

#Run script to retrieve medication data
def messageReceived(self, params, packet):
 
 print(packet.payload)
 if "Active".encode() in packet.payload:
  os.system('sudo python3 /home/pi/Medication/retrieveData.py')
  GPIO.output(ledPin, GPIO.HIGH)
  time.sleep(1)
  GPIO.output(ledPin, GPIO.LOW)
 else:
     print("Unknown Message Received")

#Subscribe to AWS IoT Core - Callback messageReceived function when message received
myMQTTClient = AWSIoTMQTTClient("RPI")
 
myMQTTClient.configureEndpoint("asxxquscsq3dr-ats.iot.us-east-1.amazonaws.com", 8883)
certRootPath = '/home/pi/Medication/certs/'
myMQTTClient.configureCredentials("{}aws-root-cert.pem".format(certRootPath), "{}private-key.pem.key".format(certRootPath), "{}iot-cert.pem.crt".format(certRootPath))
 
myMQTTClient.configureDrainingFrequency(2) # Draining: 2 Hz
myMQTTClient.configureConnectDisconnectTimeout(10) # 10 sec
myMQTTClient.configureMQTTOperationTimeout(5) # 5 sec
 
myMQTTClient.connect()
myMQTTClient.subscribe("MedicationSystem", 1, messageReceived)
print("subscribed")

def looper():
    while True:
        time.sleep(5)

looper()

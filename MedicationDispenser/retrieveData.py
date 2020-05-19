import mysql.connector
from mysql.connector import Error
from crontab import CronTab
import sys
from datetime import datetime
import json
from urllib import request, parse

try:
    #Retrieve medication data from API
    datalog={}
    datalog["userID"]=1
    datalog=json.dumps(datalog)
    myData=[('Data',datalog)]
    data = parse.urlencode(myData).encode()
    req = request.Request("https://mayar.abertay.ac.uk/~1604475/MedWeb/API/retrieveData.php", data=data)
    with request.urlopen(req) as response:
        page = response.read().decode()
        
    medData = json.loads(page)

    cron = CronTab(user='pi')
    cron.remove_all() #Remove all crontab tasks
    #Insert cron task to update medication data at midnight
    retriever = cron.new(command='sudo python3 /home/pi/Medication/retrieveData.py >> /home/pi/logs.log 2>&1')
    retriever.minute.on(0)
    retriever.hour.on(0)
    cron.write()

    #For each medication data retrieved convert datetime and insert cron task
    for count, medicationID in enumerate(medData):
        x = datetime.strptime(medicationID["dateTime"], '%Y-%m-%d %H:%M:%S')
        month = (x.strftime("%m"))
        day = (x.strftime("%d"))
        hour = (x.strftime("%H"))
        minute = (x.strftime("%M"))

        job = cron.new(command='sudo python3 /home/pi/Medication/controller.py %s %s %s %s'%(medicationID["pillOneAmount"], medicationID["pillTwoAmount"], medicationID["pillThreeAmount"], medicationID["medicationID"]))
        job.minute.on(minute)
        job.hour.on(hour)
        job.day.on(day)
        job.month.on(month)
        cron.write()
    print("Medication times scheduled: ", (count+1))
        
except Error as e:
    print("Could not retrieve medication data: ", e)

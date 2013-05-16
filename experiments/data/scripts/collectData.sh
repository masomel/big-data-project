#!/bin/bash
./telApp.sh | telnet > "../appleStocks/appleMay$1$2" 
sleep 5
./telAppMob.sh | telnet > "../appleStocks/appleMay$1$2Mobile" 
sleep 5
./telCnn.sh | telnet > "../cnn/cnnMay$1$2" 
sleep 5
./telCnnMob.sh | telnet > "../cnn/cnnMay$1$2Mobile" 
sleep 5
./telEcon.sh | telnet > "../econ/econMay$1$2" 
sleep 5
./telEconMob.sh | telnet > "../econ/econMay$1$2Mobile" 
sleep 5
./telGoog.sh | telnet > "../googleStocks/googleMay$1$2" 
sleep 5
./telGoogMob.sh | telnet > "../googleStocks/googleMay$1$2Mobile" 
sleep 5
./telPrince.sh | telnet > "../princeton/princetonMay$1$2" 
sleep 5
./telPrinceMob.sh | telnet > "../princeton/princetonMay$1$2Mobile" 
sleep 5
./telStock.sh | telnet > "../stocks/financeMay$1$2" 
sleep 5
./telStockMob.sh | telnet > "../stocks/financeMay$1$2Mobile" 
sleep 5
./telWeath.sh | telnet > "../weather/weatherMay$1$2" 
sleep 5
./telWeathMob.sh | telnet > "../weather/weatherMay$1$2Mobile" 
sleep 5
./telNYtimes.sh | telnet > "../nytimes/nytimesMay$1$2"
sleep 5
./telNYtimesMob.sh | telnet > "../nytimes/nytimesMay$1$2Mobile" 
sleep 5
./telHuff.sh | telnet > "../huffpost/huffpostMay$1$2"
sleep 5
./telHuffMob.sh | telnet > "../huffpost/huffpostMay$1$2Mobile" 
sleep 5

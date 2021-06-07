#!/usr/bin/python
from myCheck import *
from yourCheck import *

print ("INFO: Running app again")


platform = { 'a': 1, 'b': 2 }

print ("My check")
one, two = checkmyPlatform (platform)
print (one, two)

print ("Your check")
one, two = checkyourPlatform (platform)
print (one, two)

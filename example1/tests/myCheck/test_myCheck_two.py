# test_myCheck.py

import sys
sys.path.append('src/myCheck')

import myCheck
import pytest

def test_yourCheck_two():
    platform = { 'a': 1, 'b': 2 }
    print ("My check test")
    one, two = myCheck.checkmyPlatform (platform)
    assert two == 2
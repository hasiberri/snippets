# test_with_pytest.py

import sys
sys.path.append('src/yourCheck')

import yourCheck
import pytest

def test_yourCheck():
    platform = { 'a': 1, 'b': 2 }
    print ("Your check test one")
    one, two = yourCheck.checkyourPlatform (platform)
    assert one == 1, two ==2

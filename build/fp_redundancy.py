#!/usr/bin/env python


import sys
import math

def search_for_bp(s,e, true_bps):
  for bp in true_bps:
    if s==bp[0] and e==bp[1]:
      return bp
    if s<bp[0]:
      break
  return None

def close(p,l, margin):
  if math.fabs(p-l) <= margin:
    return True
  return False 

def search_for_bp_margin(s,e,true_bps, margin):
  for bp in true_bps:
    if close(s,bp[0],margin) and close(e,bp[1],margin) :
      return bp
  return None



f=open(sys.argv[1])
lines1=f.readlines()
f.close()
margin = int(sys.argv[2])

fps = []
redundant = 0
for l in lines1:
  if l.split()[0] == "FP":
    s=int(l.split()[3])
    e=int(l.split()[4])
    if search_for_bp_margin(s,e,fps,margin) == None:
      fps.append([s,e])
      print l,
    else:
      redundant +=1
  else:
    print l,
print str(redundant) +" redundant fasle positives detected"

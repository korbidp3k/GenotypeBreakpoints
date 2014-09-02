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

def search_for_half_bp_margin(s,e,true_bps, margin):
  for bp in true_bps:
    if close(s,bp[0],margin) or close(e,bp[1],margin) :
      return bp
  return None



f=open(sys.argv[1])
lines1=f.readlines()
f.close()
margin = int(sys.argv[2])

fns = []
half= 0
for l in lines1:
  if l.split()[0] == "FN":
    fns.append([int(l.split()[3]), int(l.split()[4])])

for l in lines1:
  if l.split()[0] == "FP":
    s=int(l.split()[3])
    e=int(l.split()[4])
    if search_for_half_bp_margin(s,e,fns,margin) == None:
      print l,
    else:
      half+=1
  else:
    print l,
print str(half) +" half true false positives detected"

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
  inter = None
  for bp in true_bps:
    if close(s,bp[0],margin) and close(e,bp[1],margin) :
      if bp[2]:
        inter = bp
      else :
        return bp
    if s+margin<bp[0]:
      break
  return inter


if len(sys.argv) < 4:
  print "Usage: <BP1 (sorted)> <BP2> <error margin for wobbly BPs> "
  sys.exit(0)

f=open(sys.argv[1])
lines1=f.readlines()
f = open (sys.argv[2])
lines2 = f.readlines()
f.close()
margin = int(sys.argv[3])

true_bps = []
for l in lines1:
  true_bps.append([int(l.split()[0]), int(l.split()[1]), False, l.split()[2], l.split()[3]])

further_analysis = []
redundant_bps = 0

for l in lines2:
  s=int(l.split()[0])
  e=int(l.split()[1])
   
  bp=search_for_bp(s,e,true_bps) 
  if bp == None:
    bp=search_for_bp(e,s,true_bps)
  if bp == None:
    further_analysis.append(l)
  elif not bp[2]:
    print "TP	",
    print bp[3],
    print "	",
    print bp[4],
    print "     ",
    print l,
    bp[2] = True
  else :
    redundant_bps += 1

nonsense = [] 

for l in further_analysis:
  s=int(l.split()[0])
  e=int(l.split()[1])

  bp=search_for_bp_margin(s,e,true_bps, margin)
  if bp ==None:
    bp=search_for_bp_margin(e,s,true_bps, margin)
  if bp == None:
    nonsense.append(l)
  elif not bp[2]:
    print "TP(wobble)	",
    print bp[3],
    print "     ",
    print bp[4],
    print "	",
    print l,
    bp[2] = True
  else :
    redundant_bps += 1

print "%d events not resolved... false positives?" %(len(nonsense))
for l in nonsense:
  size = math.fabs(int(l.split()[0]) - int(l.split()[1]))
  print "FP	",
  print "     X	%d	" %(size),
  print l,


print "Events not recalled (false negatives):"
for bp in true_bps:
  if not bp[2]:
    print "FN	%s	%s	%d	%d" %(bp[3],bp[4],bp[0], bp[1])
   # print "FN %d	%d" %(bp[0], bp[1])

print "%d redundant breakpoints reported" %(redundant_bps)

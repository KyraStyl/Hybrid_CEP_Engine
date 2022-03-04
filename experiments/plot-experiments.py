#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Feb 25 18:52:26 2022

@author: kyrastyl
"""

import matplotlib.pyplot as plt
import numpy as np
import statistics as s

x = [10, 20, 50]

saseM = [13415770, 63052327, 3074136320]
cetM = [19799703239, 22213272393, 30608804906]

saseA = [4978165, 16895467, 895601015]
cetA = [11600587399, 12506073754, 15495292399]


barWidth = 0.3

title = "Max and Avg Latency for QStock2"
diag = "Latency (ns)"
#log = True
log = False
#"""
plt.plot(x, saseM, color='indigo', linewidth=0.8, marker='s', label="SASE-max")
plt.plot(x, cetM, color='c', linewidth=0.8, marker='s', label="CET-max")
plt.plot(x, saseA, color='orchid', linewidth=0.8, marker='s', label="SASE-avg")
plt.plot(x, cetA, color='darkslategrey', linewidth=0.8, marker='s', label="CET-avg")


plt.xticks(x)
#plt.yticks(np.arange(0, 100000000000, 1000000))
"""
br1 = np.arange(len(x))
br2 = [x + barWidth for x in br1]

plt.bar(br1, cet, color ='c', width = barWidth, label="CET")
plt.bar(br2, sase, color ='m', width = barWidth, label="SASE")
plt.xticks([r + barWidth for r in range(len(x))],x)
"""
plt.xlabel("Window Length")
plt.ylabel(diag)

plt.title(title)

if log:
    plt.yscale('log')
plt.legend(loc ="upper left")
plt.savefig('exp2-lat-stock-q2.eps', format='eps')
plt.show()

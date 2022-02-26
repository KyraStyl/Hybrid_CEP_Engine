#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Feb 25 18:52:26 2022

@author: kyrastyl
"""

import matplotlib.pyplot as plt
import numpy as np

x = [10, 20, 50, 100, 150, 200]
sase = [0.4, 1, 2, 20, 266, 11520]
cet = [20, 22, 27, 55, 120, 1080]

barWidth = 0.3

title = "Memory Usage for QKite"
diag = "Memory Usage (MB)"
#log = True
log = False

plt.xticks(x)
plt.plot(x, sase, color='m', linewidth=0.8, marker='s', label="SASE")
plt.plot(x, cet, color='c', linewidth=0.8, marker='s', label="CET")

#br1 = np.arange(len(x))
#br2 = [x + barWidth for x in br1]

#plt.bar(br1, cet, color ='c', width = barWidth, label="CET")
#plt.bar(br2, sase, color ='m', width = barWidth, label="SASE")
#plt.xticks([r + barWidth for r in range(len(x))],x)

plt.xlabel("Window Length")
plt.ylabel(diag)

plt.title(title)

if log:
    plt.yscale('log')
plt.legend(loc ="upper left")
plt.savefig('exp1-time2-kite.eps', format='eps')
plt.show()

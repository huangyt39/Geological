import numpy as np
import cv2
import pdb

img_path = '6.png' 
img = cv2.imread(img_path)
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

detected_edges = cv2.GaussianBlur(gray, (3, 3), 0)
detected_edges = cv2.Canny(detected_edges, 1, 200, apertureSize=3)

areaIndex = np.copy(detected_edges)
areaIndex[detected_edges == 0] = False
areaIndex[detected_edges == 255] = True

#create moving window
n,m  = detected_edges.shape[0], detected_edges.shape[1]
# pdb.set_trace()
partNum = 500

#caculate the broken rate of each area
brokenRate = np.zeros(partNum)
for i in range(partNum):
    brokenRate[i] = sum(sum(areaIndex[:, m//partNum*i:m//partNum*(i+1)]))

#replace 20% areas
sortedBrokenRate = np.copy(brokenRate)
sortedBrokenRate.sort()
accpectRate = sortedBrokenRate[int(0.70 * len(sortedBrokenRate))]
usedIndex = np.zeros(partNum)
for i in range(partNum):
    if brokenRate[i] > accpectRate:
        replaceIndex = i
        while replaceIndex < partNum - 5:
            if brokenRate[replaceIndex] < accpectRate and usedIndex[replaceIndex] == 0:
                usedIndex[replaceIndex] = 1
                break          
            replaceIndex += 1
        # pdb.set_trace()
        img[:, m//partNum*i:m//partNum*(i+1)] = img[:, m//partNum*replaceIndex : m//partNum*(replaceIndex+1)]
# broken_area = 

#remove dark areas
grayImg = cv2.GaussianBlur(gray, (3, 3), 0)
darkIndex = np.copy(grayImg)
darkIndex[grayImg <= 80] = False
darkIndex[grayImg > 80] = True
# pdb.set_trace()

colNum, lineNum = 50, 10
perCol, perLine = n//colNum, m//lineNum
darkRate = np.zeros((colNum, lineNum))
for i in range(colNum):
    for j in range(lineNum):
        darkRate[i, j] = sum(sum(darkIndex[i*perCol:(i+1)*perCol, j*perLine:(j+1)*perLine]))
pdb.set_trace()

sortedDarkRate = np.copy(darkRate)
sortedDarkRate.flatten()
sortedDarkRate.sort()
# accpectDarkRate = sortedDarkRate[0, int(0.9*len(sortedDarkRate[0]))]
print("accpectDarkRate: ", accpectDarkRate)
usedIndex2 = np.zeros((colNum, lineNum))
for i in range(colNum):
    for j in range(lineNum):
        if darkRate[i, j] < accpectDarkRate:
            replaceColIndex = i
            while replaceColIndex > 0:
                if darkRate[replaceColIndex, j] < accpectDarkRate and usedIndex2[replaceColIndex, j] == 0:
                    usedIndex2[replaceColIndex, j] = 1
                    break
                replaceColIndex -= 1
            if replaceColIndex == 0:
                while replaceColIndex < lineNum:
                    if darkRate[replaceColIndex, j] < accpectDarkRate and usedIndex2[replaceColIndex, j] == 0:
                        usedIndex2[replaceColIndex, j] = 1
                        break
                    replaceColIndex += 1
            img[i*perCol:(i+1)*perCol, j*perLine:(j+1)*perLine] = img[replaceColIndex*perCol:(replaceColIndex+1)*perCol, j*perLine:(j+1)*perLine]

cv2.imshow("img", img)

cv2.waitKey(10000)
import numpy as np
import cv2
import pdb
import matplotlib.pyplot as plt

def fillCracks(picindex):
    #读取图片转灰度图 高斯模糊
    img = cv2.imread('./pic/boxes/box'+ str(picindex) +'.png')
    gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (3, 3), 0)
    m,n = gray.shape

    #二值化 保存灰度图
    grayBorder = 150
    gray[gray > grayBorder] = 255
    gray[gray <= grayBorder] = 0
    # cv2.imwrite("gray.jpg", gray)

    #计算每列高光像素的数量，依每组10像素进行分组，求每组的均值
    colLightSum = np.sum(gray, axis=0)//255
    mean = np.mean(colLightSum)
    pixelNumPerSet = 10
    lightSet = np.zeros((n//pixelNumPerSet, 2))
    for index in range(n//pixelNumPerSet):
        lightSet[index, 0] = index
        lightSet[index, 1] = np.mean(colLightSum[index*pixelNumPerSet:(index+1)*pixelNumPerSet])

    # 掐头去尾，达到总均值才视为石头，否则删去
    lightSet = lightSet[lightSet[:, 1] > mean//3]
    head_cut = min(lightSet[0, 0], len(lightSet)//9)
    tail_cut = max(lightSet[-1, 0], len(lightSet)//9*8)

    #删去高光较低组(前百分之二十)
    lightSet_sort = list(np.copy(lightSet))
    lightSet_sort.sort(key=lambda x:x[1])
    lightSet_sort = np.array(lightSet_sort)
    lightBorder = lightSet_sort[int(0.10*len(lightSet_sort)), 1]
    lightSet = lightSet[lightSet[:, 1] > lightBorder]

    #生成新图像
    newImg = np.array(np.array([[[0]*3]*m])).reshape(m, 1, 3)
    lightSet_sortbyIndex = np.copy(lightSet)
    for index in range(len(lightSet_sortbyIndex) - 2):
        # if lightSet_sortbyIndex[index, 0] >= head_cut and lightSet_sortbyIndex[index, 0] <= tail_cut:
            imgIndex = int(lightSet_sortbyIndex[index, 0])
            newImg = np.append(newImg, img[:, imgIndex*pixelNumPerSet:(imgIndex+1)*pixelNumPerSet], axis=1)

    #保存新图像
    cv2.imwrite('./pic/result/box'+ str(picindex) +'result.png', newImg)
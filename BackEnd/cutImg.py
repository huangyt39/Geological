import numpy as np
import cv2
import matplotlib.pyplot as plt
import pdb
import config
import os

def cutImg(img_path, img_name):
    # img_path = './samplepic/sample1.jpg' # 图片路径
    img = cv2.imread(img_path)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # 用Sobel算子进行边缘识别
    detected_edges = cv2.Sobel(img, cv2.CV_64F, 0, 1)

    # 二值化识别图像
    m,n,l = detected_edges.shape
    matrixedges = np.zeros((m,n))

    matrixedges = np.sum(detected_edges, axis=2)
    matrixedges[matrixedges < 500] = 0
    matrixedges[matrixedges >= 500] = 255 

    # 保存识别图像
    # cv2.imwrite('matrixedges.png', matrixedges)

    # 计算每行高光像素的数量，分为十五组，求每组最大值和所在位置
    lineLightSum = np.sum(matrixedges,axis=1)//255
    lightSet = np.zeros((15, 2))
    for i in range(15):
        lightSet[i, 0] = np.argmax(lineLightSum[i*len(lineLightSum)//15:(i+1)*len(lineLightSum)//15]) + i*len(lineLightSum)//15
        lightSet[i, 1] = np.max(lineLightSum[i*len(lineLightSum)//15:(i+1)*len(lineLightSum)//15])

    #利用均值和距离进行排除
    mean = np.mean(lightSet[:, 1])
    lightSet = lightSet[lightSet[:, 1] > mean/2]
    i = 0
    while i < len(lightSet) - 1:
        if lightSet[i + 1, 0] - lightSet[i, 0] <= m/15:
            deleteIndex = np.argmin(lightSet[i:i+2, 1])
            lightSet = np.delete(lightSet, i+deleteIndex, axis=0)
        else:
            i += 1

    #保存切条后的图片
    lightSet = np.delete(lightSet, 1 ,axis=1)
    if lightSet[0] > 200:
        lightSet = np.insert(lightSet, 0, 0, axis=0)
    if lightSet[-1] < m - 200:
        lightSet = np.append(lightSet, m-1)

    for index in range(1, len(lightSet)):
        tempimagename = os.path.join(
                config.IMAGE_AFTER_PROCESS_DIR, img_name[:img_name.find('.jpg')] + str(index) + '.jpg')
        cv2.imwrite(tempimagename, img[int(lightSet[index]):int(lightSet[index+1])])
    
    return len(lightSet) - 1
    
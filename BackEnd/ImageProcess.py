import numpy as np
import cv2
import config
import os


def imageProcess(img_path, img_name):
    # img_path = '1.jpg' # 图片路径
    img = cv2.imread(img_path)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    detected_edges = cv2.GaussianBlur(gray, (3, 3), 0)
    detected_edges = cv2.Canny(detected_edges, 100, 300, apertureSize=3)

    listx = np.zeros(detected_edges.shape[0])

    for i in range(0, detected_edges.shape[0]):
        index = detected_edges[i] == 255
        listx[i] = (detected_edges[i][index].size)

    # print(list)
    l = []
    index = listx > 50
    for i in range(0, index.size):
        if index[i] == True:
            l.append(i)

    count = 0  # 岩芯的层数

    imageNames = list()
    for i in range(0, len(l) - 1):
        if l[i + 1] - l[i] > 50:
            count = count + 1
            tempimagename = os.path.join(
                config.IMAGE_AFTER_PROCESS_DIR, img_name[:img_name.find('.jpg')] + str(count) + '.jpg')
            cv2.imwrite(tempimagename, img[l[i]: l[i + 1]])
            imageNames.append(tempimagename)

    return imageNames
    # cv2.imshow("img", detected_edges)
    # cv2.imshow("img2", img[507:605])

    # cv2.waitKey(0)
    # cv2.destroyAllWindows()

import cutImg
import fillCracks

def imgProcess(img_path, img_name):
    count = cutImg.cutImg(img_path, img_name)
    imageNames = list()
    for i in range(1, count+1):
        tmpname = fillCracks.fillCracks(i, img_name)
        imageNames.append(tmpname)
    return imageNames

import cutImg
import fillCracks

def imgProcess():
    img_path = './samplepic/sample5.jpg'
    count = cutImg.cutImg(img_path)
    for i in range(count):
        fillCracks.fillCracks(i)
    print("Finish")

if __name__ == "__main__":
    imgProcess()
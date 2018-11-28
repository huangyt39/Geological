from flask import Flask, request, session, send_file
from database import db_session
from models import User
import os
from datetime import timedelta
import config
import time
import predict
from werkzeug import secure_filename
import imgProcess

ALLOWED_EXTENSIONS=set(['dbf','prj','sbn','sbx','shp','shx','xml'])
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.',1)[1] in ALLOWED_EXTENSIONS

app = Flask(__name__)


@app.route('/')
def index():
    # 主页
    app.logger.debug("index page is accessed")
    return "index page"


@app.route('/login', methods=['GET'])
def loginPage():
    '''
    离开：username，password，字符串
    '''
    username = request.args['username']
    password = request.args['password']
    if username is None or password is None:
        app.logger.error("not completed form of Login")
        return "not completed form"
    users = list(User.query.filter_by(name=username))
    if len(users) == 0:
        app.logger.error("login: no such user")
        return "no such user"
    else:
        if users[0].password == password:
            session['isLogin'] = True
            session['username'] = username
            app.logger.debug(
                "login user : {0}, password : {1}".format(username, password))
            return "successfully login"
        else:
            session['isLogin'] = False
            app.logger.debug(
                "login user : {0} wrong password".format(username))
            return "wrong password"


@app.route('/register', methods=['GET'])
def registerPage():
    '''
    args : username str
    args : password str
    '''
    username = request.args['username']
    password = request.args['password']
    if username is None or password is None:
        app.logger.error('register form error')
        return "not completed form"
    users = list(User.query.filter_by(name=username))
    if len(users) != 0:
        app.logger.error("username has been registered")
        return "username has been registered"
    else:
        newUser = User(name=username, password=password)
        try:
            db_session.add(newUser)
            db_session.commit()
            db_session.close()
        except Exception as e:
            print(e)
            session['isLogin'] = False
            app.logger.error("new user creation error")
            return "database error"
        session['isLogin'] = True
        session['username'] = username
        app.logger.debug("user {0} created and logined".format(username))
        return "user register successfully"


@app.route('/login', methods=['POST'])
def loginWithPost():
    username = request.form['username']
    password = request.form['password']
    if username is None or password is None:
        app.logger.error("login form error")
        return "not completed form"
    users = list(User.query.filter_by(name=username))
    if len(users) == 0:
        app.logger.error("login no such user")
        session['isLogin'] = False
        return "no such user"
    else:
        if users[0].password == password:
            app.logger.debug("user {0} login ".format(username))
            return "successfully login"
        else:
            app.logger.error(
                "user {0} login error : wrong password".format(username))
            return "wrong password"


@app.route('/register', methods=['POST'])
def registerWithPost():
    username = request.form['username']
    password = request.form['password']
    if username is None or password is None:
        app.logger.error('register form error')
        return "not completed form"
    users = list(User.query.filter_by(name=username))
    if len(users) != 0:
        app.logger.error("username has been registered")
        return "username has been registered"
    else:
        newUser = User(name=username, password=password)
        try:
            db_session.add(newUser)
            db_session.commit()
            db_session.close()
        except Exception as e:
            print(e)
            app.logger.error("new user creation error")
            return "database error"
        session['username'] = username
        session['hasLogin'] = True
        app.logger.debug("user {0} created and logined".format(username))
        return "user register successfully"


@app.route("/uploadimage", methods=['POST'])
def handleImage():
    #if not ('isLogin' in session) or not session['isLogin']:
    #    app.logger.debug("uploadimage without login")
    #    return "sorry, you haven't login"
    # username = request.form['username']
    image = request.files['image']
    print(image)
    if image is None:
        return "no image"
    else:
        imagefilename = str(time.time()) + ".jpg"
        # userpath = os.path.join(
        #         config.IMAGE_STORE_DIR, username)
        # image.save(os.path.join(userpath, imagefilename))
        image.save(os.path.join(config.IMAGE_STORE_DIR, imagefilename))
        session["imagefilename"] = imagefilename
        return "image save successfully"

@app.route("/getimagesnumber", methods=["GET"])
def returnSplitedImageNumber():
    try:
        imagefilename = session["imagefilename"]
    except Exception as e:
        return str(0)
    print(imagefilename)
    imagefilepath = os.path.join(config.IMAGE_STORE_DIR, imagefilename)
    print(imagefilepath)
    splitImagesNames = imgProcess.imgProcess(imagefilepath, imagefilename)
    print(splitImagesNames)
    return str(len(splitImagesNames))


@app.route("/getimage", methods=['GET'])
def returnSplitedImage():
    imageIndex = request.args["imageindex"]
    currentImageFileName = session['imagefilename'][
        :session['imagefilename'].find('.jpg')] + imageIndex + ".jpg"
    currentImageFilePath = os.path.join(
        config.IMAGE_AFTER_PROCESS_DIR, currentImageFileName)
    return send_file(currentImageFilePath, mimetype="image/jpeg")

@app.route("/uploadfiles", methods=['POST'])
def handleArcgisFiles():
    if not ('isLogin' in session) or not session['isLogin']:
        app.logger.debug("uploadfiles without login")
        return "sorry, you haven't login"
    files = request.files
    print (request.files)
    if files is None:
        return "no ArcgisFiles"
    else:
        # image.save(os.path.join(config.IMAGE_STORE_DIR, image.filename))
        for key in files.keys():
            file = files[key]
            if file:
                _, savepath = os.path.split(file.filename)
                print(os.path.join(config.ARCGISDATA_DIR, savepath))
                file.save(os.path.join(config.ARCGISDATA_DIR, savepath))
        return "files save successfully"

@app.route("/getpredictresult", methods=['GET'])
def returnPredictImage():
    predict.predict('范围', '类别')
    predictresultindex = request.args["predictresultindex"]
    predictImageFileName = {0:'data.png', 1:'result.png','0':'data.png', '1':'result.png'}
    currentImageFilePath = os.path.join(
        config.IMAGE_PREDICTED, predictImageFileName[predictresultindex])
    return send_file(currentImageFilePath, mimetype="image/jpeg")

@app.route("/checkLogin")
def checkLogin():
    try:
        if session['isLogin']:
            return "user {0} has login".format(session['username'])
        else:
            return "user hasn't login"
    except Exception as e:
        return "user hasn't login"


@app.route("/listalldata")
def listAllData():
    users = list(User.query.all())
    users = [(user.id, user.name, user.password) for user in users]
    return str(users)


@app.teardown_request
def shutdown_session(exception=None):
    db_session.remove()


if __name__ == '__main__':
    app.config["SECRET_KEY"] = os.urandom(24)
    app.config['PERMANENT_SESSION_LIFETIME'] = timedelta(days=7)
    app.run(debug=True, host='0.0.0.0')

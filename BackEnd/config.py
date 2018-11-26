import os
DATABASE_FILENAME = 'database.db'
DATABASE_PATH = 'sqlite:///' + \
    os.path.join(os.path.dirname(__file__), DATABASE_FILENAME)
DATABASE_USERNAME = 'USER'
DATABASE_PASSWORD = 'PASSWORD'

IMAGE_STORE_DIR = os.path.join(os.path.dirname(__file__), 'images')
IMAGE_AFTER_PROCESS_DIR = os.path.join(os.path.dirname(__file__), 'afterprocess')

ARCGISDATA_DIR = os.path.join(os.path.dirname(__file__), 'predictdata')
IMAGE_PREDICTED = os.path.join(os.path.dirname(__file__), 'predictimg')

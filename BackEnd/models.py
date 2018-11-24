from sqlalchemy import Column, Integer, String
from database import Base
import config


class User(Base):
    __tablename__ = 'users'
    id = Column(Integer, primary_key=True)
    name = Column(String(20), unique=True)
    password = Column(String(20))
    picdir = Column(String(50))

    def __init__(self, name=None, password=None):
        self.name = name
        self.password = password
        self.picdir = config.IMAGE_STORE_DIR + name

    def __repr__(self):
        return "<User {0}>".format(self.name)

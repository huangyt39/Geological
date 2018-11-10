from sqlalchemy import create_engine, Column, String
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
import config

engine = create_engine(config.DATABASE_PATH, convert_unicode=True)
db_session = scoped_session(sessionmaker(autocommit=False,
                                         autoflush=False,
                                         bind=engine))

Base = declarative_base()
Base.query = db_session.query_property()


def init_db():
    print(config.DATABASE_PATH)
    Base.metadata.create_all(bind=engine)

# def drop_all():
#     import models

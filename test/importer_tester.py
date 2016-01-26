__author__ = 'umanoidTyphoon'

from src.importer import NetflixDatasetImporter

if __name__ == '__main__':
    netflix_importer = NetflixDatasetImporter()
    netflix_importer.import_movies()
    netflix_importer.import_training_set()
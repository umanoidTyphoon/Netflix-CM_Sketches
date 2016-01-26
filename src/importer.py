__author__ = 'umanoidTyphoon'

import __init__ as init


class NetflixDatasetImporter:
    def __init__(self):
        self.connection        = init.connect_to_db()
        self.connection_cursor = self.connection.cursor()
        self.dataset_directory = "../datasets/Netflix_prize_dataset/download"
        self.movie_titles_file = "movie_titles.txt"
        self.training_set_dir  = self.dataset_directory + "/training_set"

    def import_movies(self):
        query = "CREATE TABLE movies(MovieID INTEGER PRIMARY KEY, YearOfRelease INT,  Title VARCHAR(256))"
        self.connection_cursor.execute(query)

        with open(self.dataset_directory + "/" + self.movie_titles_file, 'r') as csv_file:
            csv_reader = init.csv.reader(csv_file)

            for csv_row in csv_reader:
                movie_id     = csv_row[0]
                release_year = csv_row[1]
                title        = unicode(csv_row[2].replace("'", "''"), errors='replace')

                query = "INSERT INTO Movies VALUES(" + movie_id + "," + release_year + ",'" + title + "')"
                self.connection_cursor.execute(query)

        self.connection.commit()

    def import_training_set(self):
        query = "CREATE TABLE training_set(MovieID INT, CustomerID INT, Rating INT,  Date VARCHAR(10))"
        self.connection_cursor.execute(query)

        training_set_files = init.os.listdir(self.training_set_dir)
        for csv_filename in training_set_files:
            movie_id = int(csv_filename.split("_")[1].split(".txt")[0])

            row = -1
            with open(self.training_set_dir + "/" + csv_filename, 'r') as csv_file:
                csv_reader = init.csv.reader(csv_file)
                for csv_row in csv_reader:
                    row += 1
                    if row == 0:
                        continue

                    customer_id = csv_row[0]
                    rating      = csv_row[1]
                    date        = csv_row[2]

                    query = "INSERT INTO training_set VALUES(" + str(movie_id) + "," + customer_id + "," + rating + ",'" \
                            + date + "')"
                    self.connection_cursor.execute(query)

        self.connection.commit()

    def import_training_set_from_csv(self, csv_file):
        query = "CREATE TABLE reduced_training_set(MovieID INT, CustomerID INT, Rating INT,  Date VARCHAR(10))"
        self.connection_cursor.execute(query)

        with open(csv_file, 'r') as in_file:
            csv_reader = init.csv.reader(in_file)
            for csv_row in csv_reader:
                movie_id    = csv_row[0]
                customer_id = csv_row[1]
                rating      = csv_row[2]
                date        = csv_row[3]

                query = "INSERT INTO reduced_training_set VALUES(" + str(movie_id) + "," + customer_id + "," + \
                        rating + ",'" + date + "')"
                self.connection_cursor.execute(query)

        self.connection.commit()





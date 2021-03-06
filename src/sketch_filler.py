__author__ = 'umanoidTyphoon'

import __init__ as init


class SketchCreatorFiller:
    def __init__(self):
        self.connection        = init.connect_to_db()
        self.connection_cursor = self.connection.cursor()

    def fill_sketch_by_movie(self, customer_id, cm_sketch):
        # query = "SELECT movieid, rating FROM training_set WHERE customerid = " + str(customer_id)
        query = "SELECT movieid, rating FROM reduced_training_set WHERE customerid = " + str(customer_id)

        self.connection_cursor.execute(query)
        for row in self.connection_cursor:
            movie_id = row[0]
            rating   = row[1]

            cm_sketch.update(movie_id, rating)

        return cm_sketch

    def fill_sketch_by_user(self, movie_id, cm_sketch):
        query = "SELECT customerid, rating FROM reduced_training_set WHERE movieid = " + str(movie_id)

        self.connection_cursor.execute(query)
        for row in self.connection_cursor:
            print row
            user_id = row[0]
            rating  = row[1]

            cm_sketch.update(user_id, rating)

        return cm_sketch

    def create_user_sketch(self, customer_id):
        LOW_ROUNDING = 0.49

        l1_norm       = self.compute_l1_norm(customer_id)
        epsilon       = LOW_ROUNDING / l1_norm
        sketch_window = self.compute_user_sketch_window(customer_id)

        sketch  = init.Sketch(10**-9, epsilon, sketch_window, 0)

        return sketch

    def create_movie_sketch(self, movie_id):
        sketch_window = self.compute_movie_sketch_window(movie_id)
        epsilon       = init.math.e / (sketch_window + 1)

        sketch  = init.Sketch(10**-9, epsilon, sketch_window, 0)

        return sketch

    def compute_l1_norm(self, customer_id):
        l1_norm = 0
        # query   = "SELECT rating FROM training_set WHERE customerid = " + str(customer_id)
        query   = "SELECT rating FROM reduced_training_set WHERE customerid = " + str(customer_id)

        self.connection_cursor.execute(query)
        for row in self.connection_cursor:
            rating   = row[0]
            l1_norm += rating

        return l1_norm

    def compute_user_sketch_window(self, customer_id):
        # query = "SELECT COUNT(rating) FROM training_set WHERE customerid = " + str(customer_id)
        query = "SELECT COUNT(rating) FROM reduced_training_set WHERE customerid = " + str(customer_id)
        self.connection_cursor.execute(query)
        rating_count = self.connection_cursor.fetchone()[0]

        print "SKETCH CREATOR FILLER :: User %d has rated %d movies" % (customer_id, rating_count)

        return rating_count

    def compute_movie_sketch_window(self, movie_id):
        query = "SELECT COUNT(rating) FROM reduced_training_set WHERE movieid = " + str(movie_id)
        self.connection_cursor.execute(query)
        rating_count = self.connection_cursor.fetchone()[0]

        print "SKETCH CREATOR FILLER :: Movie %d has been seen by %d users" % (movie_id, rating_count)

        return rating_count
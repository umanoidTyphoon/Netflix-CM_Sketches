__author__ = 'umanoidTyphoon'

import __init__ as init


class SketchCreatorFiller:
    def __init__(self):
        self.connection        = init.connect_to_db()
        self.connection_cursor = self.connection.cursor()

    def fill_sketch(self, customer_id, cm_sketch):
        query = "SELECT movieid, rating FROM training_set WHERE customerid = " + str(customer_id)

        self.connection_cursor.execute(query)
        for row in self.connection_cursor:
            movie_id = row[0]
            rating   = row[1]

            cm_sketch.update(movie_id, rating)

        return cm_sketch

    def create_sketch(self, customer_id):
        LOW_ROUNDING = 0.49

        l1_norm       = self.compute_l1_norm(customer_id)
        epsilon       = LOW_ROUNDING / l1_norm
        sketch_window = self.compute_sketch_window(customer_id)

        sketch  = init.Sketch(10**-9, epsilon, sketch_window, 0)

        return sketch

    def compute_l1_norm(self, customer_id):
        l1_norm = 0
        query   = "SELECT rating FROM training_set WHERE customerid = " + str(customer_id)

        self.connection_cursor.execute(query)
        for row in self.connection_cursor:
            rating   = row[0]
            l1_norm += rating

        return l1_norm

    def compute_sketch_window(self, customer_id):
        query = "SELECT COUNT(rating) FROM training_set WHERE customerid = " + str(customer_id)
        self.connection_cursor.execute(query)
        rating_count = self.connection_cursor.fetchone()[0]

        print "SKETCH CREATOR FILLER :: User %d has rated %d movies" % (customer_id, rating_count)

        return rating_count

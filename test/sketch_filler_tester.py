__author__ = 'umanoidTyphoon'

from src.sketch_filler import SketchCreatorFiller

import src.__init__ as init

if __name__ == '__main__':
    # customer_id = 1
    # customer_id = 490
    #
    # netflix_sketch_creator_filler = SketchCreatorFiller()
    #
    # cm_sketch = netflix_sketch_creator_filler.create_sketch(customer_id)
    # cm_sketch = netflix_sketch_creator_filler.fill_sketch(customer_id, cm_sketch)

    netflix_sketch_creator_filler = SketchCreatorFiller()
    with open("../datasets/Netflix_prize_dataset/download/users_2016-01-25_21-42-32.txt", 'r') as rand_usr_file:
        user_ids = map(int, rand_usr_file.readlines())

        for customer_id in user_ids:
            cm_sketch = netflix_sketch_creator_filler.create_sketch(customer_id)
            cm_sketch = netflix_sketch_creator_filler.fill_sketch(customer_id, cm_sketch)

            init.util.serialize_sketch(str(customer_id) + ".p", cm_sketch)

            break
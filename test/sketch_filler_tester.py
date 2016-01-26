__author__ = 'umanoidTyphoon'

from src.sketch_filler import SketchCreatorFiller

if __name__ == '__main__':
    # customer_id = 1
    customer_id = 885013
    netflix_sketch_creator_filler = SketchCreatorFiller()

    cm_sketch = netflix_sketch_creator_filler.create_sketch(customer_id)
    cm_sketch = netflix_sketch_creator_filler.fill_sketch(customer_id, cm_sketch)
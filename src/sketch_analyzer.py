__author__ = 'umanoidTyphoon'

import __init__ as init


class SketchAnalyzer:
    def __init__(self, serialized_cm_sketch):
        self.serialized_cm_sketch = serialized_cm_sketch

    def print_sketch_stats(self):
        deserialized_sketch = init.util.deserialize_sketch(self.serialized_cm_sketch)

        print "SKETCH ANALYZER TESTER >> SKETCH ANALYZER :: Sketch width, depth and window: %d - %d -  %d" % (
                                                                                                  deserialized_sketch.w,
                                                                                                  deserialized_sketch.d,
                                                                                                  deserialized_sketch.k)
        sketch_keys   = []
        sketch_values = []
        for integer in range(2000000):
            sketch_value = deserialized_sketch.get(integer)

            if sketch_value != 0:
                print "SKETCH ANALYZER TESTER >> SKETCH ANALYZER :: Right value get with key %d" % integer
                sketch_keys.append(integer)
                sketch_values.append(sketch_value)

        print sketch_keys; print(sketch_values)

        cm_sketch = init.Sketch(10**-9, 0.2258, 12, 0)
        i = 0
        while i < len(sketch_keys):
            cm_sketch.update(sketch_keys[i], sketch_values[i])
            i += 1

        print "________________________________________________________________________________________________________"

        for integer in range(170000):
            sketch_value = cm_sketch.get(integer)

            if sketch_value != 0:
                print "SKETCH ANALYZER TESTER >> SKETCH ANALYZER :: Right value get with key %d" % integer
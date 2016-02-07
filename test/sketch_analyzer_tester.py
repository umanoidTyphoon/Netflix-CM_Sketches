__author__ = 'umanoidTyphoon'

from src.sketch_analyzer import SketchAnalyzer

if __name__ == '__main__':
    analyzer = SketchAnalyzer("1.p")

    analyzer.print_sketch_stats()
# Instructor Code Review Feedback

**Contact**: Dr. Yiji Zhang (yiji.zhang@northwestern.edu)

**Purpose of This Document**:
The instructor will perform code review with respect to software design, error handling, format and style on the main branch every week starting Week 6 using the letter grade A standards.
The following chapters of the textbook are considered: Chapter 1, 2, 3, 4, 5, 6, 7, 9, and 10. The corresponding lectures are considered, too.

## Week 7 Code Review
I have read every line of production code currently in the main branch. Again, I didn't find any violations. Very good job!

Just one tiny suggestion for you to consider:

In the BoardGeometry class,
```
    // Integer coordinates keep shared corners and edge midpoints identical before conversion to pixels.
    private static final List<Point2D> CORNER_OFFSETS = List.of(new Point2D(0, -2), new Point2D(1, -1),
            new Point2D(1, 1), new Point2D(0, 2), new Point2D(-1, 1), new Point2D(-1, -1));
```
can be formatted as 
```
    // Integer coordinates keep shared corners and edge midpoints identical before conversion to pixels.
    private static final List<Point2D> CORNER_OFFSETS = List.of(new Point2D(0, -2),
                                                                new Point2D(1, -1),
                                                                new Point2D(1, 1),
                                                                  ...);
```
by aligning the `new` so it's easier for people to see the logic behind the numbers.

## Week 6 Code Review
I have read every line of production code currently in the main branch. I didn't find any violations. Good job! I will do code review again next week when there's even more code.

BTW your coding-standards.md file is AMAZING! Good work!

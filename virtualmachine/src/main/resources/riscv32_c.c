struct Coordinate {
    double x;
    double y;
};

double distance(const struct Coordinate *from, const struct Coordinate *to) {
    double x = to->x - from->x;
    double y = to->y - from->y;
    return (x*x + y*y);
}

int main() {
    struct Coordinate from = { .x = 5, .y = 100 };
    struct Coordinate to = { .x = 0, .y = 0 };
   // printf("%f\n", distance(&from, &to));
    distance(&from, &to);
    return 0;
}

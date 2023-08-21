# Rocky in Docker

## Running Rocky in Docker 

If you don't have Java on your machine, you can run Rockstar within a Docker container. There is a [`dockstar`](https://github.com/gaborsch/rocky/tree/master/dockstar) command for you, where everything works just like with the `rockstar` command, but the image is run within Docker. The only difference is that you have to replace `\` path separators to `/` unix-style on command line. 

For example:

```
bin/dockstar help
bin/dockstar programs/fizzbuzz.rock
bin/dockstar debug programs/fizzbuzz.rock
```

## Creating a Docker image

There is a `dockerfile` available, so you can create a docker image using the following command:

```
docker build -t rockstar .
```

Once created, you can use the container to run Rocky with the standard Docker commands. 
For example:

* Get help: `docker run --rm rockstar help`
* Run a program: `docker run --rm -v ${pwd}:/local rockstar /local/programs/gameoflife.rock`
* Run a program (with input): `docker run --rm -v ${pwd}:/local --interactive --tty rockstar /local/programs/modulus.rock`


# Flood It

[![build](https://github.com/MediaComem/floodit/actions/workflows/build.yml/badge.svg)](https://github.com/MediaComem/floodit/actions/workflows/build.yml)
[![license](https://img.shields.io/github/license/MediaComem/floodit)](https://opensource.org/licenses/MIT)

A web game developed with [Spring Boot][spring-boot] in [Java][java] for the
backend and [Angular][angular] in [JavaScript][js] for the frontend.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Requirements](#requirements)
- [Initial setup](#initial-setup)
- [Run the automated tests](#run-the-automated-tests)
- [Run the application in development mode](#run-the-application-in-development-mode)
- [Run the application in production mode](#run-the-application-in-production-mode)
- [Updating](#updating)
- [Configuration](#configuration)
  - [Environment variables](#environment-variables)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Requirements

To run the application, you will need:

- The [Java][java] 21 JRE
- [PostgreSQL][postgresql] 12, 13, 14, 15, 16 or 17

Additionally, to compile the backend and frontend, you will need:

- The [Java][java] 21 JDK
- [Maven][maven] 3+
- [Node.js][node] 22

## Initial setup

- Create a PostgreSQL user named `floodit` for the application:

  ```bash
  $> sudo -u postgres createuser --interactive --pwprompt floodit
  Enter password for new role:
  Enter it again:
  Shall the new role be a superuser? (y/n) n
  Shall the new role be allowed to create databases? (y/n) n
  Shall the new role be allowed to create more new roles? (y/n) n
  ```

  > You should answer no to all questions. The `floodit` user does not need
  > any special privileges.

- Create a PostgreSQL database named `floodit` and owned by the
  `floodit` user:

  ```bash
  $> sudo -u postgres createdb --owner floodit floodit
  ```

- Clone the repository:

  ```bash
  $> git clone https://github.com/MediaComem/floodit.git
  ```

- Download dependencies and compile the application (_grab a coffee, this might
  take a while the first time_):

  ```bash
  $> cd floodit
  $> mvn clean install -Pskip-test
  ```

- Install frontend dependencies:

  ```bash
  $> npm ci
  ```

- You can configure the application in one of two ways:

  - Either set any of the [documented environment
    variables](#environment-variables), for example:

    ```bash
    export FLOODIT_DATABASE_HOST=localhost
    export FLOODIT_DATABASE_PORT=5432
    export FLOODIT_DATABASE_NAME=floodit
    export FLOODIT_DATABASE_USERNAME=floodit
    export FLOODIT_DATABASE_PASSWORD=mysecretpassword
    export FLOODIT_SERVER_PORT=5000
    ```

  - Or create a local configuration file based on the provided sample:

    ```bash
    cp backend/config/application-default.local.sample.yml backend/config/application-default.local.yml
    ```

    Edit `backend/config/application-default.local.yml` with your favorite editor:

    ```bash
    nano backend/config/application-default.local.yml
    vim backend/config/application-default.local.yml
    ```

    Read the instructions contained in the file and fill in the database
    connection parameters and web endpoint settings.

    > The `backend/config/application-default.local.yml` file will be ignored by
    > Git.

## Run the automated tests

Follow these instructions to execute the project's [automated test
suite][automated-tests]:

- Create a separate PostgreSQL test database named `floodit-test` also owned
  by the `floodit` user:

  ```bash
  $> sudo -u postgres createdb --owner floodit floodit-test
  ```

- Configure the application for testing:

  - Either set any of the [documented environment
    variables](#environment-variables), for example:

    ```bash
    export FLOODIT_TEST_DATABASE_HOST=localhost
    export FLOODIT_TEST_DATABASE_PORT=5432
    export FLOODIT_TEST_DATABASE_NAME=floodit-test
    export FLOODIT_TEST_DATABASE_USERNAME=floodit
    export FLOODIT_TEST_DATABASE_PASSWORD=mysecretpassword
    ```

  - Or create a local configuration file based on the provided sample:

    ```bash
    cp backend/config/application-test.local.sample.yml backend/config/application-test.local.yml
    ```

    Edit `backend/config/application-test.local.yml` with your favorite editor:

    ```bash
    nano backend/config/application-test.local.yml
    vim backend/config/application-test.local.yml
    ```

    Read the instructions contained in the file and fill in the database
    connection parameters.

    > The `backend/config/application-test.local.yml` file will be ignored by
    > Git.

- Run the automated tests:

  ```bash
  $> mvn clean test
  ```

You should see no errors.

> For more information, read the [tests' source code in the `test`
> directory](./backend/src/test/java/ch/comem/archidep/floodit).

## Run the application in development mode

You can run the backend application manually by executing the following command
from the repository:

```bash
mvn spring-boot:run
```

> The backend application runs on port 5000 by default. If that port is already
> in use, you can use the `server.port` parameter in the local configuration
> file or the `$FLOODIT_SERVER_PORT` environment variable to switch to another
> port, for example:
>
> ```bash
> $> FLOODIT_SERVER_PORT=5001 mvn spring-boot:run
> ```

You can run the frontend application manually by executing the following command
from the repository:

```bash
cd frontend
npm start
```

> The frontend application runs on port 4200 by default. If that port is already
> in use or you want to use another port, you can add the `--port <number>`
> option to switch to another port.
>
> By default, the frontend application is only available locally. Add the
> `--host 0.0.0.0` option to the last command to make it available to external
> clients.
>
> You must add an additional `--` before any other option due to how `npm`
> passes options through, for example:
>
> ```bash
> cd frontend
> npm start -- --host 0.0.0.0 --port 3000
> ```
>
> If you have changed the backend server port to a different port than 5000 you
> should also set the `$FLOODIT_FRONTEND_API_PROXY_PORT` variable to the same
> port number when running the frontend in development mode below, for example:
>
> ```bash
> FLOODIT_FRONTEND_API_PROXY_PORT=5001 npm start -- --host 0.0.0.0 --port 3000
> ```

## Run the application in production mode

Build the backend application:

```bash
mvn clean install -Pskip-test
```

Build the frontend application:

```bash
npm run build
```

The static web files of the frontend will be saved in the `frontend/dist`
directory:

```bash
ls frontend/dist/
```

Run the backend by executing the packaged [JAR file][jar]. This must be done
from the `backend` directory in the repository:

```bash
cd backend
java -jar target/floodit-1.0.0-SNAPSHOT.jar
```

You can use your favorite web server and reverse proxy (e.g. Apache/nginx) to
serve the frontend's files. `/api` requests should be proxied to the backend
listening on port 5000 (or whatever port you configured).

## Updating

To update the application after getting the latest changes, execute the
following commands in the application's directory:

```bash
# Rebuild the backend application:
$> mvn clean install -Pskip-test

# Update frontend dependencies:
$> npm ci

# Rebuild the frontend application:
$> npm run build
```

You may then restart the application.

## Configuration

You can configure the application in one of two ways:

- Either create a `backend/config/application-default.local.yml` file in the
  application's directory (see the
  `backend/config/application-default.local.sample.yml` sample file).
- Or use the environment variables documented below.

### Environment variables

| Environment variable              | Default value  | Description                                                                              |
| :-------------------------------- | :------------- | :--------------------------------------------------------------------------------------- |
| `FLOODIT_DATABASE_HOST`           | `localhost`    | The host at which the PostgreSQL database can be reached.                                |
| `FLOODIT_DATABASE_PORT`           | `5432`         | The port at which the PostgreSQL database can be reached.                                |
| `FLOODIT_DATABASE_NAME`           | `floodit`      | The name of the PostgreSQL database.                                                     |
| `FLOODIT_DATABASE_USERNAME`       | `floodit`      | The PostgreSQL user to connect as.                                                       |
| `FLOODIT_DATABASE_PASSWORD`       | -              | The password of the PostgreSQL user.                                                     |
| `FLOODIT_SERVER_PORT`             | `5000`         | The port the HTTP server listens on.                                                     |
| `FLOODIT_FRONTEND_API_PROXY_PORT` | `5000`         | The local port the frontend will proxy API requests to when running in development mode. |
| `FLOODIT_TEST_DATABASE_HOST`      | `localhost`    | The host at which the PostgreSQL test database can be reached.                           |
| `FLOODIT_TEST_DATABASE_PORT`      | `5432`         | The port at which the PostgreSQL test database can be reached.                           |
| `FLOODIT_TEST_DATABASE_NAME`      | `floodit-test` | The name of the PostgreSQL test database.                                                |
| `FLOODIT_TEST_DATABASE_USERNAME`  | `floodit`      | The PostgreSQL user to connect as for the test database.                                 |
| `FLOODIT_TEST_DATABASE_PASSWORD`  | -              | The password of the PostgreSQL user for the test database.                               |

[angular]: https://angular.io
[automated-tests]: https://en.wikipedia.org/wiki/Test_automation
[jar]: https://en.wikipedia.org/wiki/JAR_(file_format)
[java]: https://www.java.com
[js]: https://developer.mozilla.org/en-US/docs/Web/JavaScript
[maven]: https://maven.apache.org
[node]: https://nodejs.org
[npm]: https://www.npmjs.com
[postgresql]: https://www.postgresql.org
[spring-boot]: https://spring.io/projects/spring-boot
[systemd]: https://en.wikipedia.org/wiki/Systemd

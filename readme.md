[![Release](https://jitpack.io/v/umjammer/vavi-util-archive-sandbox.svg)](https://jitpack.io/#umjammer/vavi-util-archive-sandbox)
[![Java CI](https://github.com/umjammer/vavi-util-archive-sandbox/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-util-archive-sandbox/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-util-archive-sandbox/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-util-archive-sandbox/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)
[![Parent](https://img.shields.io/badge/Parent-vavi--util--archive-pink)](https://github.com/umjammer/vavi-util-archive)

# vavi-util-archive-sandbox

🌏 Extract the world more!

extract all archive types in the same way!</br>
archives are able to mount as fuse also using [vavi-nio-file-archive](https://github.com/umjammer/vavi-apps-fuse/tree/master/vavi-nio-file-archive)
and [vavi-net-fuse](https://github.com/umjammer/vavi-apps-fuse/tree/master/vavi-net-fuse)

### Status

| name     | mathod    | read  | write | comment                                | library                                                                                                                                      |
|----------|-----------|-------|-------|----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| d88      | archiving | ✅     | -     |                                        |                                                                                                                                              |
| rar      | archiving | -     | -     | COM                                    | [jacob](https://github.com/freemansoft/jacob-project)                                                                                        |
| rar      | archiving | ✅     | -     |                                        | [unrar](https://github.com/ezrec/AROS-mirror/tree/0386b906ada312f26ac0eb1f42c7676df50d00d2/contrib/aminet/util/arc/unrar) c port             |
| stuffit  | archiving | 🚧    | -     |                                        | [unsit](https://github.com/aros-development-team/contrib/blob/bf975ce15093ac48d9e76f6e65660b521a617bdc/aminet/util/arc/unsit/unsit.c) c port |
| cab      | archiving | -     | -     | COM                                    | [jacob](https://github.com/freemansoft/jacob-project)                                                                                        |
| cab      | archiving | ✅     | -     |                                        | [KBA](https://www.madobe.net/archiver/lib/activex/kba.html)                                                                                  |
| cab      | archiving | 🚧    | -     |                                        | [ibex](http://git.megacz.com/?p=org.ibex.util.git;a=tree;f=src/org/ibex/util)                                                                |
| asar     | archiving | ✅*    | -     | fail at file size is smaller than spec | [asar](https://github.com/Scroetchen/asar)                                                                                                   |
| asar     | archiving | 🚧    | -     | json paser too much strict             | [asar4j](https://github.com/anatawa12/asar4j)                                                                                                |
| xar      | archiving | ✅     | -     |                                        | [xar](https://github.com/sprylab/xar)                                                                                                        |
| arj      | archiving | -     | -     | COM                                    | [jacob](https://github.com/freemansoft/jacob-project)                                                                                        |
| arj      | archiving | 🚧    | -     |                                        | [c](https://github.com/tripsin/unarj)                                                                                                        |
| arj      | archiving | ✅*    | -     | unsupported files exist                | [commons-compress](https://commons.apache.org/proper/commons-compress/)                                                                      |
| archiveR | streaming | 🚧    | -     | TODO engine.io-nize                    | [ArchiveR](https://github.com/prog-ai/ArchivR)                                                                                               |
| sevenzip | archiving | ✅     | -     | multi, **arm64 not supported**         | [sevenzipjbinding](https://github.com/borisbrodski/sevenzipjbinding)                                                                         |
| bortli   | streaming | 🚧    | -     |                                        | [bortli](https://github.com/google/brotli)                                                                                                   |

<sub>* chosen as spi</sub>

## Install

 * [maven](https://jitpack.io/#umjammer/vavi-util-archive-sandbox)

## Usage

```java
    Archive archive = Archives.getArchive(Paths.get("foo/bar.xar").toFile());
    Path outDir = Paths.get("foo/bar");
    for (Entry entry : archive.entries()) {
        Files.copy(archive.getInputStream(entry), outDir.resolve(entry.getName()));
    }
```

## References

 * https://github.com/prog-ai/ArchivR
 * https://github.com/temisu/ancient
 * https://github.com/google/brotli

### License

 * unrar ... [GPL](http://www.gnu.org/licenses/gpl.html)
 * [ibex](https://util.ibex.org/src/org/ibex/util/) ... LGPL

## TODO

 * ~~make it enable to compile~~
 * ~~asar~~
 * https://github.com/prog-ai/ArchivR (wip)
 * https://bitbucket.org/magli143/exomizer/wiki/Home
 * brotli (wip)

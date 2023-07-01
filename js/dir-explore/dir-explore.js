const fs = require("node:fs");
const path = require("node:path/posix");
const { performance } = require("node:perf_hooks");

function walk(basepath, filepath, log) {
  const counts = {
    total: 0n,
    dirs: 0n,
    files: 0n,
    links: 0n,
    other: 0n,
    noaccess: 0n,
  };
  try {
    const entries = fs.readdirSync(path.join(basepath, filepath), { withFileTypes: true });
    counts.total += BigInt(entries.length);
    for (const entry of entries) {
      if (log) {
        console.log(`${"  ".repeat(filepath.split("/").length)}${formatDirent(entry)}`);
      }
      if (entry.isDirectory()) {
        counts.dirs++;
        const result = walk(basepath, `${filepath}/${entry.name}`, true);
        for (const key in counts) {
          counts[key] += result.counts[key];
        }
      } else if (entry.isFile()) {
        counts.files++;
      } else if (entry.isSymbolicLink()) {
        counts.links++;
      } else {
        counts.other++;
      }
    }
  } catch (err) {
    if (["EACCES", "EPERM"].includes(err.code)) {
      counts.noaccess++;
    }
  }
  return { filepath, counts };
}

function explore(filepath = "/", log = false) {
  try {
    fs.accessSync(filepath);
  } catch (err) {
    throw new Error(`directory not found: ${filepath}`);
  }
  if (!fs.statSync(filepath).isDirectory()) {
    throw new Error(`not a directory: ${filepath}`);
  }
  if (log) {
    console.log(`${path.basename(path.resolve(filepath))}/`);
  }
  const result = walk(filepath, ".", log);
  if (log) {
    console.log(`Entries:       ${result.counts.total.toString().padStart(8)}`);
    console.log(`Directories:   ${result.counts.dirs.toString().padStart(8)}`);
    console.log(`Files:         ${result.counts.files.toString().padStart(8)}`);
    if (result.counts.links) console.log(`Links:         ${result.counts.links.toString().padStart(8)}`);
    if (result.counts.other) console.log(`Other:         ${result.counts.other.toString().padStart(8)}`);
    if (result.counts.noaccess) console.log(`Inaccessible:  ${result.counts.noaccess.toString().padStart(8)}`);
  }
  return result;
}

function formatDirent(dirent) {
  const suffix = 
    dirent.isDirectory() ? "/"
  : dirent.isFile() ? ""
  : dirent.isSymbolicLink() ? ` -> ${fs.readlinkSync(path.join(dirent.path, dirent.name))}`
  : dirent.isFIFO() ? " [p]"
  : dirent.isSocket() ? " [s]"
  : dirent.isCharacterDevice() ? " [c]"
  : dirent.isBlockDevice() ? " [b]"
  : "";
  return `${dirent.name.replace(/[\x00-\x1F\x7F]/g, "?")}${suffix}`;
}

function parseArgs(args) {
  const result = { paths: [] };
  for (const arg of args) {
    result.paths.push(arg);
  }
  return result;
}

function formatTime(time) {
  let ms = Math.trunc(time);
  if (ms < 1000) return `${ms}ms`;
  let s = ms / 1000;
  if (s < 60) return `${s.toFixed(2)}s`;
  let m = Math.trunc(s / 60);
  s = Math.trunc(s % 60);
  if (m < 60) return `${m}m${s.toString().padStart(2, "0")}s`;
  let h = Math.trunc(m / 60);
  m %= 60;
  return `${h}h${m.toString().padStart(2, "0")}m${s.toString().padStart(2, "0")}s`;
}

if (require.main === module) {
  const { paths } = parseArgs(process.argv.slice(2));
  if (paths.length === 0) explore(".", true);
  else for (const filepath of paths) {
    try {
      explore(filepath, true);
    } catch (err) {
      console.error(err.message);
    }
  }
  console.log(`Finished in ${formatTime(performance.now())}`);
}

module.exports = explore;

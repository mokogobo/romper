# Romper

Romper is a declarative modeling language.

## Installation

1. Clone git repository.
2. Run with IntelliJ.

## Usage

#### `open`

Opens and executes a Romper script.

#### `namespace`

#### `type`

#### `has`

#### `let`

#### `structure|struct`

#### `set`

#### `add`

#### `remove|rem|rm`

#### `name`

#### `browse|view`

#### `describe|ds`

#### `transcript|history`

#### `exit`

## Example Usage

``import file <filename>``
Imports the file _filename_ in the directory relative to the working directory. Note that this file can import files, itself, enabling file hierarchies to be loaded with a single top-level file.

add project
Adds a project to the current workspace. Assigns it an index for ordering relative to other projects in the workspace, a per-session UID, and a UUID.

edit project

add device

edit device

add port

edit port

list attributes

list attribute <identifier>

## Compatible Configurations

### Mode
XXX null ⟷ null
XXX None ⟷ null
Power ⟷ Power
...
uart(rx) ⟷ uart(tx)
uart(tx) ⟷ uart(rx)

### Direction
Input ⟵ Output
Input ⟵ Bidirectional
Output ⟶ Bidrectional
Bidrectional ⟷ Bidirectional

### Voltage
TTL – TTL
CMOS – CMOS

## Data Model

Romper stores its data model in a collection of YAML files.

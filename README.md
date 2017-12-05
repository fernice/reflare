# reFlare

Re-Flare provides a compatibility mode for Flare allowing to used Flare as a Look and Feel in Swing
Its purpose is also to provide tools and functionalities to progressively refactor projects build onto Java Swing, but also to close the compatibility gap between
different major versions of Flare, as Flare is not backwards compatible.

## Structure

reFlare is referenced in Flare under the namespace FLARE-RE.

Internally reFlare uses namespace each starting with REFLARE:

| Namespace      | Description | Internal References | External References |
|----------------|-------------|---------------------|---------------------|
| RENDER         | Provides the _RenderPipeline_ that allows us to render directly inside a _paint(awt.Graphics)_ method, or in our case in a UI. | none | FLARE-RENDER |
| LNF            | Provides a LookAndFeel that uses Flare to render. | RENDER | none |
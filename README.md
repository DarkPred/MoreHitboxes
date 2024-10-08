# More Hitboxes

This library provides both Fabric and Forge support for multiple hitboxes on mobs. The fabric version adds an Entity 
similar to Forge's PartEntity while the Forge version expands on the PartEntity.

## Adding the dependency

Add the following repo:\
`https://dl.cloudsmith.io/public/darkpred-mods/more-hitboxes/maven/` \
and artifact:
`com.github.darkpred.morehitboxes:morehitboxes-${loader}-${minecraft_version}:${more_hitboxes_version}` \
to your build.gradle. Available loaders are fabric, forge and common(for multi loader setups)

## Setting up the hitboxes

To set up the hitboxes for a mob you first need to implement the correct interface and create the HitboxData.

```java
public class Example extends Mob implements MultiPartEntity<Example> {
    private final EntityHitboxData<Prehistoric> hitboxData = EntityHitboxDataFactory.create(this);

    @Override
    public EntityHitboxData<Prehistoric> getEntityHitboxData() {
        return hitboxData;
    }
}
```

To add the hitboxes, create a json file at `data/{modId}/hitboxes/{entityTypeKey}.json`. Here a hitbox can be created by
defining the width, height and local position(bottom center)

```json
{
    "elements": [
        {
            "name": "body",
            "pos": [0, 0, 0],
            "width": 17.6,
            "height": 20.8
        },
        {
            "name": "head",
            "pos": [0, 13.4, 15.8],
            "width": 11.2,
            "height": 8
        }
    ]
}
```

To attach the hitboxes to GeckoLib animations implement the GeckoLib interface instead.
```java
public class GeckoLibExample extends Mob implements GeckoLibMultiPartEntity<Example> {
    private final EntityHitboxData<Prehistoric> hitboxData = EntityHitboxDataFactory.create(this);

    @Override
    public EntityHitboxData<Prehistoric> getEntityHitboxData() {
        return hitboxData;
    }
}
```

To have the animation override the static position, first add an (ideally) empty bone to the model and reference its name 
in the json file.

```json
{
    "elements": [
        {
            "name": "body",
            "pos": [0, 0, 0],
            "width": 17.6,
            "height": 20.8,
            "ref": "body_hitbox"
        },
        {
            "name": "head",
            "pos": [0, 13.4, 15.8],
            "width": 11.2,
            "height": 8,
            "ref": "head_hitbox"
        }
    ]
}
```

## Template
This project was set up with [MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template/tree/1.18.2/examples)

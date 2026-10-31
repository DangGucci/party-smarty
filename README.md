# Party Smarty

A [RuneLite](https://runelite.net) plugin that rejoins a saved party when you log in.

Enable **Party Smarty** and the built-in **Party** plugin. Open the Party Smarty icon under **Hub Party Panel**, or the Party Smarty config, and check **Auto rejoin**. Set **Party passphrase** to the passphrase from the Party panel (**Copy passphrase**). Joining a party also stores that passphrase in the field. The sidebar and the config edit the same saved values.

Each time the client starts, and each time you log in, Party Smarty joins that party. Open the Party panel to see members who are connected to it. No Join click is needed.

**Leave party** stays left until the next login. The next login joins the saved party again while Auto rejoin is checked.

The checkbox and passphrase are stored in your RuneLite config profile. With profile sync on, they follow that profile to another computer. Close the client on one computer before opening it on another so the profile finishes syncing.

## Running

From this folder:

```
.\gradlew run
```

Jagex account sign-in: https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts

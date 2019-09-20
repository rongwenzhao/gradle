/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.provider

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class PropertyJavaInterOpIntegrationTest extends AbstractPropertyLanguageInterOpIntegrationTest {
    def setup() {
        pluginDir.file("build.gradle") << """
            plugins { 
                id("java-library")
            }
            dependencies {
                api gradleApi()
            }
        """
        pluginDir.file("src/main/java/SomeTask.java") << """
            import ${DefaultTask.name};
            import ${Property.name};
            import ${ListProperty.name};
            import ${SetProperty.name};
            import ${MapProperty.name};
            import ${ObjectFactory.name};
            import ${TaskAction.name};
            import ${Inject.name};
            import ${Internal.name};

            public class SomeTask extends DefaultTask {
                private final Property<Boolean> flag;
                private final Property<String> message;
                private final ListProperty<Integer> list;
                private final SetProperty<Integer> set;
                private final MapProperty<Integer, Boolean> map;
                
                @Inject
                public SomeTask(ObjectFactory objectFactory) {
                    flag = objectFactory.property(Boolean.class);
                    message = objectFactory.property(String.class);
                    list = objectFactory.listProperty(Integer.class);
                    set = objectFactory.setProperty(Integer.class);
                    map = objectFactory.mapProperty(Integer.class, Boolean.class);
                }
                
                @Internal
                public Property<Boolean> getFlag() {
                    return flag;
                }

                @Internal
                public Property<String> getMessage() {
                    return message;
                }
                
                @Internal
                public ListProperty<Integer> getList() {
                    return list;
                }
                
                @Internal
                public SetProperty<Integer> getSet() {
                    return set;
                }
                
                @Internal
                public MapProperty<Integer, Boolean> getMap() {
                    return map;
                }
                
                @TaskAction
                public void run() {
                    System.out.println("flag = " + flag.get());
                    System.out.println("message = " + message.get());
                    System.out.println("list = " + list.get());
                    System.out.println("set = " + set.get());
                    System.out.println("map = " + map.get());
                }
            }
        """
    }

    @Override
    void pluginSetsValues() {
        pluginDir.file("src/main/java/SomePlugin.java") << """
            import ${Project.name};
            import ${Plugin.name};
            import ${Arrays.name};
            import ${Map.name};
            import ${LinkedHashMap.name};

            public class SomePlugin implements Plugin<Project> {
                public void apply(Project project) {
                    project.getTasks().register("someTask", SomeTask.class, t -> {
                        t.getFlag().set(true);
                        t.getMessage().set("some value");
                        t.getList().set(Arrays.asList(1, 2));
                        t.getSet().set(Arrays.asList(1, 2));
                        Map<Integer, Boolean> map = new LinkedHashMap<>();
                        map.put(1, true);
                        map.put(2, false);
                        t.getMap().set(map);
                    });
                }
            }
        """
    }

    @Override
    void pluginSetsCalculatedValuesUsingCallable() {
        pluginDir.file("src/main/java/SomePlugin.java") << """
            import ${Project.name};
            import ${Plugin.name};
            import ${Arrays.name};
            import ${Map.name};
            import ${LinkedHashMap.name};

            public class SomePlugin implements Plugin<Project> {
                public void apply(Project project) {
                    project.getTasks().register("someTask", SomeTask.class, t -> {
                        t.getFlag().set(project.provider(() -> true));
                        t.getMessage().set(project.provider(() -> "some value"));
                        t.getList().set(project.provider(() -> Arrays.asList(1, 2)));
                        t.getSet().set(project.provider(() -> Arrays.asList(1, 2)));
                        t.getMap().set(project.provider(() -> {
                            Map<Integer, Boolean> map = new LinkedHashMap<>();
                            map.put(1, true);
                            map.put(2, false);
                            return map;
                        }));
                    });
                }
            }
        """
    }

    @Override
    void pluginSetsCalculatedValuesUsingMappedProvider() {
        pluginDir.file("src/main/java/SomePlugin.java") << """
            import ${Project.name};
            import ${Plugin.name};
            import ${Provider.name};
            import ${Arrays.name};
            import ${Map.name};
            import ${LinkedHashMap.name};

            public class SomePlugin implements Plugin<Project> {
                public void apply(Project project) {
                    project.getTasks().register("someTask", SomeTask.class, t -> {
                        Provider<String> provider = project.provider(() -> "some value"); 
                        t.getFlag().set(provider.map(s -> !s.isEmpty()));
                        t.getMessage().set(provider.map(s -> s));
                        t.getList().set(provider.map(s -> Arrays.asList(1, 2)));
                        t.getSet().set(provider.map(s -> Arrays.asList(1, 2)));
                        t.getMap().set(provider.map(s -> {
                            Map<Integer, Boolean> map = new LinkedHashMap<>();
                            map.put(1, true);
                            map.put(2, false);
                            return map;
                        }));
                    });
                }
            }
        """
    }

    @Override
    void pluginDefinesTask() {
        pluginDir.file("src/main/java/SomePlugin.java") << """
            import ${Project.name};
            import ${Plugin.name};

            public class SomePlugin implements Plugin<Project> {
                public void apply(Project project) {
                    project.getTasks().register("someTask", SomeTask.class);
                }
            }
        """
    }
}

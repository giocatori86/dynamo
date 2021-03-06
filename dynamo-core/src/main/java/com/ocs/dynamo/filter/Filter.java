/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ocs.dynamo.filter;

import java.util.Collection;
import java.util.List;

/**
 * Interface for objects that
 * 
 * @author bas.rutten
 */
public interface Filter {

    /**
     * Evaluates an object against the filter
     * 
     * @param that
     *            the object to evaluate
     * @return
     */
    boolean evaluate(Object that);

    /**
     * Applies the filter to a collection of objects, and retunrs the objects that match the filter
     * 
     * @param collection
     * @return
     */
    <T> List<T> applyFilter(Collection<T> collection);

}
